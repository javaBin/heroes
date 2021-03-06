# JavaBin Heroes

Admin application for contributors to JavaBin and JavaZone events
and organization.

State: Close to feature complete

## Deploying new versions

* `mvn clean package azure-webapp:deploy`
  * You need to have an Azure account
  * You may need to have the Azure CLI and do `az login` first time
  * Azure WebApp name is located in `pom.xml`

## Running the application locally

1. Start the frontend bundler
   1. `cd src/main/resources/webapp`
   2. `npm install` to install NPM dependencies
   3. `npm start` to start Parcel bundler
2. Run `no.javabin.heroes.WebServer`
3. Go to `http://localhost:9093`

## Development

* Run the unit tests in Java
* Run `npm test` in `src/main/resources/webapp` to run frontend tests


## Feature

1. Admin can log in using Slack account
2. Admin can register a hero with name and email or by import from Slack
3. Admin can add achievements to hero profile
   * Admin can import heroes from previous conferences and talks
4. Admin can request hero to consent to publication
5. Hero can log in using email link or Slack account
6. Hero can review information about themselves and consent to publishing
7. Front page and API lists published heroes

## Development principles and architecture

* Self-executable Jar-file with HTTP API (Jetty)
* Authenticate users with Slack (or some external Oauth2/OpenID Connect provider)
* Frontend in React with TypeScript
* Push complexity to infrastructure code and away from domain logic code
* High test coverage on client and server sides

## TODO

### Features to version 1.0

* [x] Add/update personal information must store correct info in database
* [x] Edit achievement must show current information
* [x] Material design frontend
* [x] Material design styling
* [ ] Achievement types to be completed:
  * [x] Fix date problem with User Group achievements
  * [ ] All must have an effective date for sorting
  * [ ] Regionsleder
* [ ] Highlight most important/recent achievement
* [x] Display hero Gravatar (or Slack) picture
* [ ] Notify hero via email
* [ ] Establish session via email link
* [x] Hero can see own information

### Cleanup to version 1.0

* [x] One database table per achievement type
* [ ] Frontend error handling
* [x] Remove version 0.0.0.1 database tables and code
* [ ] Redirect to https (on Azure!)
* [ ] Consent in separate table?

### Infrastructure & tooling improvements

* [x] logevents to link to github.com
* [x] fluent-jdbc to abstract away DataSource.getConnection
* [ ] DTOs as return values and arguments for controllers
* [.] ApiServlet to have a register-like structure for annotations
* [x] mvn to run `npm test && npm run build`
* [x] Use NPM Prettier
* [ ] Keep Slack `access_token` and `refresh_token` in encrypted cookie

### Cleanup for later

* [x] Split `HeroControlPanel.tsx` into more files
* [x] Remove old admin screen in frontend

### Features for version 1.1

* [ ] Import JavaZone speakers
* [ ] Customize picture
* [ ] Import meetup events to associate with JavaBin speakers


### Minor stuff

* [ ] JSON buddy to accept UUID
