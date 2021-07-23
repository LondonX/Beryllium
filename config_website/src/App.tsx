import React, { Component, ReactNode } from 'react';
import { Container, Navbar, Table, Spinner, Button } from 'react-bootstrap';
import { TVStationDao, TVStation } from './Networker'
import TVStationView from './TVStationView';
import TVStationEditorView from './TVStationEditorView';

export default class App extends Component {

  state = {
    tvStations: null,
    editingStation: null,
  }

  private maxSort = 0;

  componentDidMount() {
    this.refresh();
  }

  render(): ReactNode {
    const tvStations = this.state.tvStations as TVStation[] | null;
    return (
      <div>
        <Navbar bg="dark" variant="dark" expand="lg" sticky="top">
          <Container>
            <Navbar.Brand>Beryllium TV</Navbar.Brand>
          </Container>
        </Navbar>
        <Container fluid style={{ marginTop: 44, marginBottom: 44 }}>
          <Table striped bordered hover variant="dark">
            <thead>
              <tr>
                <th>#</th>
                <th>Title</th>
                <th>m3u8</th>
                <th>Sort</th>
              </tr>
            </thead>
            <tbody>
              {
                (tvStations || []).map((station: TVStation, index: number) =>
                  <TVStationView
                    key={station.id}
                    station={station}
                    onItemClick={() => this.setState({ editingStation: station })}
                    onMoveClick={(isMoveUp: boolean) => this.move(station, isMoveUp)}
                    moveUpEnabled={index !== 0}
                    moveDownEnabled={index !== tvStations!.length - 1} />
                )
              }
            </tbody>
          </Table>
          <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
            {
              tvStations != null
                ? <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                  <p>{tvStations.length} station(s)</p>
                  <Button
                    style={{ width: 240 }}
                    variant="primary"
                    onClick={() => this.setState({
                      editingStation: {
                        id: 0,
                        m3u8: "",
                        sort: this.maxSort + 1,
                        title: "",
                      }
                    })}>
                    Add TV Station
                  </Button>
                </div>
                : <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
                  <Spinner hidden={tvStations != null} animation="grow" variant="primary" />
                  <p>Loading saved stations</p>
                </div>
            }
          </div>
        </Container>
        <TVStationEditorView
          editingStation={this.state.editingStation}
          onHide={(refreshRequired) => {
            this.setState({ editingStation: null });
            refreshRequired && this.refresh();
          }}
        />
      </div>
    );
  }

  refresh() {
    TVStationDao.getAll().then((stations) => {
      this.setState({ tvStations: stations });
      const last = stations[stations.length - 1];
      this.maxSort = last ? last.sort : 0;
    });
  }

  move(station: TVStation, isUp: boolean) {
    const currentStations: TVStation[] = [...(this.state.tvStations || [])];
    if (!currentStations.length) {
      return
    }
    const currentIndex = currentStations.indexOf(station);
    const targetIndex = currentIndex + (isUp ? -1 : 1);

    currentStations.splice(currentIndex, 1);
    currentStations.splice(targetIndex, 0, station);
    currentStations.forEach((s, index) => s.sort = index);
    TVStationDao.insertOrUpdate(...currentStations).then(() => {
      this.setState({ tvStations: currentStations });
    })
  }
}
