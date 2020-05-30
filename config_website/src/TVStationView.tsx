import React, { PureComponent, ReactNode } from "react";
import { TVStation } from "./Networker"
import { Button, ButtonGroup } from "react-bootstrap";

export default class TVStationView extends PureComponent<{
    station: TVStation,
    onItemClick: () => void,
    onMoveClick: (isMoveUp: boolean) => void,
    moveUpEnabled: boolean,
    moveDownEnabled: boolean,
}> {

    private moveButtonClick = false;
    render(): ReactNode {
        return (
            <tr key={this.props.station.id}
                onClick={() => {
                    this.moveButtonClick || this.props.onItemClick();
                    this.moveButtonClick = false;
                }}>
                <td>{this.props.station.id}</td>
                <td>{this.props.station.title}</td>
                <td><p style={{ wordWrap: "break-word", maxWidth: 600 }}>
                    {
                        this.props.station.m3u8
                    }
                </p></td>
                <td>
                    <ButtonGroup>
                        <Button
                            disabled={!this.props.moveUpEnabled}
                            variant="outline-primary"
                            onClick={() => {
                                this.moveButtonClick = true;
                                this.props.onMoveClick(true);
                            }}>
                            ↑
                             </Button>
                        <Button
                            disabled={!this.props.moveDownEnabled}
                            variant="outline-primary"
                            onClick={() => {
                                this.moveButtonClick = true;
                                this.props.onMoveClick(false);
                            }}>
                            ↓
                             </Button>
                    </ButtonGroup>
                </td>
            </tr>
        );
    }
}