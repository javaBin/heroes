"use strict";

var Button = ReactBootstrap.Button;
var Input = ReactBootstrap.Input;
var Grid = ReactBootstrap.Grid;
var Row = ReactBootstrap.Row;
var Col = ReactBootstrap.Col;

var NewHeroesAdmin = React.createClass({
  render: function () {
    return (
      <div className="heroes-admin-add">
        <Grid>
          <Row>
            <Col md={6}>
              <Input type="text" label="Navn" placeholder="Heltens navn"/>
            </Col>
            <Col md={6}>
              <Input type="email" label="Email" placeholder="hero@java.no"/>
            </Col>
          </Row>
          <Row>
            <Col md={6}>
              <Input type="select" label="Rolle">
                <option value="styremedlem">Styremedlem</option>
                <option value="foredragsholder-jz">Foredragsholder på JavaZone</option>
                <option value="foredragsholder">Foredragsholder på javaBin</option>
                <option value="regionsleder">Regionsleder</option>
                <option value="aktiv">Aktiv</option>
              </Input>
            </Col>
            <Col md={6}>
              <Input type="date" label="Fra dato" />
            </Col>
          </Row>
        </Grid>
        <Button bsStyle="info">Legg til</Button>
      </div>
    );
  }
});

var HeroesAdmin = React.createClass({
  render: function () {
    return (
      <div className="heroes-admin-container">
        <Grid>
          <Row>
            <Col md={12}>
              <h1>javaBin Heroes</h1>
              <NewHeroesAdmin />
            </Col>
          </Row>
        </Grid>
      </div>
    );

  }
});
ReactDOM.render(<HeroesAdmin/>, document.getElementById("heroesadmin"));
