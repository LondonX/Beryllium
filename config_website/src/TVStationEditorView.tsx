import React, { Component, ReactNode } from "react";
import { Modal, Button, Form, Spinner } from "react-bootstrap";
import { TVStation, TVStationDao } from "./Networker";

export default class TVStationEditorView extends Component<{
    editingStation: TVStation | null,
    onHide: (refreshRequired: boolean) => void,
}> {

    state = {
        isSaving: false,
        isDeleting: false,
        inputTitle: "",
        inputM3u8: "",
    }

    render(): ReactNode {
        return (
            <Modal
                show={!!this.props.editingStation}
                onHide={() => this.props.onHide(false)}
                onShow={() => this.setState({
                    titleInvalid: false,
                    m3u8Invalid: false,
                    isSaving: false,
                    isDeleting: false,
                    inputTitle: this.props.editingStation?.title || "",
                    inputM3u8: this.props.editingStation?.m3u8 || "",
                })}
                animation={false}>
                <Modal.Header closeButton>
                    <Modal.Title>Edit TV Station</Modal.Title>
                </Modal.Header>
                <Modal.Body>
                    <Form.Label>TV Station basic info.</Form.Label>
                    <Form.Group>
                        <Form.Control
                            required
                            value={this.state.inputTitle}
                            onChange={(e) => this.setState({ inputTitle: e.target.value })}
                            type={"text"}
                            isInvalid={!this.state.inputTitle}
                            placeholder="Title" />
                        <Form.Control.Feedback type="invalid">
                            Please provide a valid title.
                        </Form.Control.Feedback>
                    </Form.Group>
                    <Form.Group>
                        <Form.Control
                            required
                            value={this.state.inputM3u8}
                            onChange={(e) => this.setState({ inputM3u8: e.target.value })}
                            type={"text"}
                            isInvalid={!this.state.inputM3u8}
                            placeholder="M3U8 URL" />
                        <Form.Control.Feedback type="invalid">
                            Please provide a valid m3u8 url.
                        </Form.Control.Feedback>
                    </Form.Group>
                </Modal.Body>
                <Modal.Footer>
                    <Button
                        hidden={!this.props.editingStation?.id}
                        variant="danger"
                        onClick={() => this.delete()}>
                        {
                            this.state.isDeleting
                                ? <Spinner
                                    as="span"
                                    animation="border"
                                    size="sm"
                                    role="status"
                                    aria-hidden="true"
                                />
                                : "Delete"
                        }
                    </Button>
                    <div style={{ flex: 1 }} />
                    <Button variant="secondary" onClick={() => this.props.onHide(false)}>Close</Button>
                    <Button variant="primary" onClick={() => this.save()}>
                        {
                            this.state.isSaving
                                ? <Spinner
                                    as="span"
                                    animation="border"
                                    size="sm"
                                    role="status"
                                    aria-hidden="true"
                                />
                                : "Save changes"
                        }
                    </Button>
                </Modal.Footer>
            </Modal>
        );
    }

    save() {
        if (this.state.inputTitle && this.state.inputM3u8) {
            this.setState({ isSaving: true });
            TVStationDao.insertOrUpdate({
                id: this.props.editingStation?.id || 0,
                m3u8: this.state.inputM3u8,
                title: this.state.inputTitle,
                sort: this.props.editingStation?.sort || 0,
            }).then(() => this.props.onHide(true));
        }
    }

    delete() {
        if (!this.props.editingStation) {
            return;
        }
        if (!this.props.editingStation!.id) {
            return;
        }
        this.setState({ isDeleting: true });
        TVStationDao.delete(this.props.editingStation!.id).then(() => this.props.onHide(true));
    }
}