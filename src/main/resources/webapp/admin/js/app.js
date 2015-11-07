"use strict";

var heroes = [
  {navn: "Jaran Flaath", bidrag: "Nestleder", email: "j.f@g.com", fraDato: "2015-01-01"},
  {navn: "Anders Karlsen", bidrag: "Aktiv", email: "a.k@g.com", fraDato: "2015-01-01"},
  {navn: "Bjørn Hamre", bidrag: "Styremedlem", email: "b.h@g.com", fraDato: "2015-01-01"}
];

var Button = ReactBootstrap.Button;
var Input = ReactBootstrap.Input;
var Grid = ReactBootstrap.Grid;
var Row = ReactBootstrap.Row;
var Col = ReactBootstrap.Col;

var NewHeroForm = React.createClass({
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
              <Input type="select" label="Heltetype">
                <option value="styremedlem">Styremedlem</option>
                <option value="foredragsholder-jz">Foredragsholder på JavaZone</option>
                <option value="foredragsholder">Foredragsholder på javaBin</option>
                <option value="regionsleder">Regionsleder</option>
                <option value="aktiv">Aktiv</option>
              </Input>
            </Col>
            <Col md={6}>
              <Input type="date" label="Fra dato"/>
            </Col>
          </Row>
        </Grid>
        <Button bsStyle="info">Legg til</Button>
      </div>
    );
  }
});

var Hero = React.createClass({
  render: function () {
    var heroComponents = this.props.data.map(function (hero) {
      return (
        <Grid>
          <Row>
            <Col md={2}>
              <img height="25" src="images/hero.png"/>
            </Col>
            <Col md={5}>
              {hero.navn}
            </Col>
            <Col md={5}>
              {hero.bidrag}
            </Col>
          </Row>
        </Grid>
      );
    });
    return (
      <div className="heroes">
        {heroComponents}
      </div>
    );
  }
});

var HeroesList = React.createClass({
  render: function () {
    return (
      <div className="heroes-list">
        <h2>Helter</h2>
        <Grid>
          <Row className="heroes-list-header">
            <Col md={2}></Col>
            <Col md={5}>Navn</Col>
            <Col md={5}>Heltetype</Col>
          </Row>
          <Hero data={this.props.data}/>
        </Grid>
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
              <NewHeroForm />
              <HeroesList data={this.props.data}/>
            </Col>
          </Row>
        </Grid>
      </div>
    );

  }
});
ReactDOM.render(<HeroesAdmin data={heroes}/>, document.getElementById("heroesadmin"));
