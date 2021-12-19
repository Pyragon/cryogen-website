import React from 'react'

import { Container, Row, Col } from 'react-bootstrap';

export default function Footer() {
    return (
        <div class="footer">
            <Container>
                <Row>
                    <Col xs="4">
                        <h3>Cryogen</h3>
                    </Col>
                    <Col xs="4">
                        <h3>Community</h3>
                    </Col>
                    <Col xs="4">
                        <h3>Account</h3>
                    </Col>
                </Row>
            </Container>
        </div>
    )
}
