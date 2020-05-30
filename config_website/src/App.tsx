import React, { Component, ReactNode } from 'react';
import { TVStationDao, TVStation } from './Networker'

export default class App extends Component {

  state = {
    tvStations: null,
  }

  componentDidMount() {
    TVStationDao.getAll().then((stations) => {
      this.setState({ tvStations: stations });
    });
  }

  render(): ReactNode {
    const tvStations: TVStation[] | null = this.state.tvStations;
    return (
      <div>
        <table>
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
              (tvStations || []).map((station: TVStation) =>
                <tr key={station.id}>
                  <td>{station.id}</td>
                  <td>{station.title}</td>
                  <td><p style={{ wordWrap: "break-word" }}>{station.m3u8}</p></td>
                  <td>{station.sort}</td>
                </tr>
              )
            }
          </tbody>
        </table>
      </div>
    );
  }
}
