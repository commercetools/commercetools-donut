SPHERE.IO Donut Store
=====================

This is not a real **donut** store. This is a _free template_ for subscription ecommerce sites and it's built on top of the APIs of [SPHERE.IO](http://sphere.io), [Pactas](http://www.pactas.com) and [Paymill](https://www.paymill.com).

## Live demo
Visit a live demo of SPHERE-DONUT store at http://donut.sphere.io.

## Development [![Build Status](https://secure.travis-ci.org/commercetools/sphere-donut.png?branch=master)](http://travis-ci.org/commercetools/sphere-donut)

### Run it
- Install [Play 2.1.1](http://www.playframework.com/documentation/2.1.1/Installing).
- [Clone](http://git-scm.com/book/en/Git-Basics-Getting-a-Git-Repository#Cloning-an-Existing-Repository) sphere-donut project from GitHub.
- Run `play run` command in root project directory.
- Open your browser and point it to `http://localhost:9000`

> This app is a [Play Framework](http://www.playframework.com/documentation/2.1.1/Home) app and uses the [SPHERE Play SDK](https://github.com/commercetools/sphere-play-sdk).
 
### Develop it
- Install your favourite IDE (IntelliJ, Eclipse or Netbeans).
- Generate configuration files for your chosen IDE, following [these instructions](http://www.playframework.com/documentation/2.0.x/IDE).

More information about developing with SPHERE Play SDK at `http://sphere.io/dev/play-sdk.html`.

### Configure it
- To change to another SPHERE.IO project, modify `sphere.project`, `sphere.clientId` and `sphere.clientSecret` in `conf/application.conf`
 

### Deploy it
- Install [Heroku Toolbelt](https://toolbelt.heroku.com/) (create account if needed).
- Run `heroku create` command in root project directory, will create a new remote for Git.
- Run `git push heroku master` to push the project to Heroku and deploy it.
- Run `heroku open` to open your deployed website in a browser.

More information about deploying a Play application to Heroku [here](http://www.playframework.com/documentation/2.1.1/ProductionHeroku).

Have fun!
