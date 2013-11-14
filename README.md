SPHERE.IO Donut Store
=====================

This is not a real **donut** store. This is a _free template_ for subscription ecommerce sites and it's built on top of the APIs of [SPHERE.IO](http://sphere.io), [Pactas](http://www.pactas.com) and [Paymill](https://www.paymill.com).

## Live demo
Visit a live demo of SPHERE-DONUT store at [iwantdonuts.com](http://www.iwantdonuts.com).

## Getting started [![Build Status](https://secure.travis-ci.org/commercetools/sphere-donut.png?branch=master)](http://travis-ci.org/commercetools/sphere-donut)

### Set it up
- Install [Play 2.1.1](http://www.playframework.com/documentation/2.1.1/Installing).
- [Clone](http://git-scm.com/book/en/Git-Basics-Getting-a-Git-Repository#Cloning-an-Existing-Repository) sphere-donut project from GitHub.
- Run `play run` command in root project directory.
- Open your browser and point it to [http://localhost:9000](http://localhost:9000).
 
### Configure it

#### SPHERE.IO data
- Point to [SPHERE Login](https://admin.sphere.io/login) or register a new account with [SPHERE Signup](https://admin.sphere.io/signup).
- Go to Developers  -> API Clients to retrieve your project data.
![API Backend](https://raw.github.com/commercetools/sphere-donut/master/public/images/mc_api.png)
- To use your SPHERE.IO project, modify `sphere.project`, `sphere.clientId` and `sphere.clientSecret` in `conf/application.conf`

More about the ecommerce PaaS SPHERE.IO at http://sphere.io.

#### Pactas keys
- Go to your Pactas backend to retrieve your client data
- To use your Pactas account, modify `pactas.clientId` and `pactas.clientSecret` in `conf/application.conf`

More about recurring billing with Pactas at http://www.pactas.com.

#### PAYMILL keys
- [Register at PAYMILL](https://app.paymill.com/en-gb/auth/register) to get the (test) API keys. 
- Go to PAYMILL Cockpit -> My account -> Settings -> API keys to retrieve your keys
- To use your PAYMILL account, enter your `public key` in your Pactas account.

More information about doing payments with PAYMILL at http://www.paymill.com.
 
### Deploy it
- Install [Heroku Toolbelt](https://toolbelt.heroku.com/) (create account if needed).
- Run `heroku create` command in root project directory, will create a new remote for Git.
- Run `git push heroku master` to push the project to Heroku and deploy it.
- Run `heroku open` to open your deployed website in a browser.

> More information about deploying a Play application to Heroku [here](http://www.playframework.com/documentation/2.1.1/ProductionHeroku).

### Develop it

- Install your favourite IDE (preferably IntelliJ, Eclipse or Netbeans).
- Generate configuration files for your chosen IDE, following [these instructions](http://www.playframework.com/documentation/2.0.x/IDE).
- Run `play` command in root project directory.
- Inside Play Shell, type `clean test` for compiling and testing it.

> This app is a [Play Framework](http://www.playframework.com/documentation/2.1.1/Home) app and uses the [SPHERE Play SDK](http://sphere.io/dev/play-sdk.html).

Have fun!
