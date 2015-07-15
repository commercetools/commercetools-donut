#SPHERE.IO Donut Store
=====================

This is not a real **donut** store. This is a _free template_ for subscription ecommerce sites and it's built on top of the APIs of [SPHERE.IO](http://sphere.io), [Pactas](http://www.pactas.com) and [Paymill](https://www.paymill.com).

:warning: This template is built based on the [SPHERE Play SDK](https://github.com/commercetools/sphere-play-sdk) that is now DEPRECATED! :warning:   
If you are looking for a template built using the new [SPHERE JVM SDK](https://github.com/sphereio/sphere-jvm-sdk) please have a look at [Sunrise](https://github.com/sphereio/sphere-sunrise) instead.

## Live demo
Visit a live demo of SPHERE-DONUT store at [iwantdonuts.com](http://www.iwantdonuts.com).

## Getting started [![Build Status](https://secure.travis-ci.org/commercetools/sphere-donut.png?branch=master)](http://travis-ci.org/commercetools/sphere-donut)

### Set it up
- Install [Play 2.1.5](http://www.playframework.com/documentation/2.1.x/Installing).
- [Clone](http://git-scm.com/book/en/Git-Basics-Getting-a-Git-Repository#Cloning-an-Existing-Repository) sphere-donut project from GitHub. or download it as [zip file](https://github.com/commercetools/sphere-donut/archive/master.zip).
- Run `play run` command in root project directory.
- Open your browser and point it to [http://localhost:9000](http://localhost:9000).

### Configure it

#### SPHERE.IO data
- Point to [SPHERE Login](https://admin.sphere.io/login) or register a new account with [SPHERE Signup](https://admin.sphere.io/signup).
- Go to `Developers -> API Clients` to retrieve your project data.
![API Backend](https://raw.github.com/commercetools/sphere-donut/master/public/images/mc_api.png)
- To use your SPHERE.IO project, modify `sphere.project`, `sphere.clientId` and `sphere.clientSecret` in `conf/application.conf`.

More about the ecommerce PaaS SPHERE.IO at [http://sphere.io](http://sphere.io).

#### Pactas keys
- [Login to the Pactas sandbox](https://sandbox.pactas.com) or [create a new Pactas sandbox account](https://sandbox.pactas.com/#/signup).
- Go to `Settings -> Pactas Apps -> My Apps` and create a new OAuth client. Make sure to select "Confidential" for the client type.
![PactasAppRegister](https://raw.github.com/commercetools/sphere-donut/master/public/images/pactas-register-app.png)
- Copy the newly created client id and secret to `pactas.clientId` and `pactas.clientSecret` in `conf/application.conf`.

More about recurring billing with Pactas at http://www.pactas.com.

#### PAYMILL keys
- [Register at PAYMILL](https://app.paymill.com/en-gb/auth/register) to get the (test) API keys.
- Go to `PAYMILL Cockpit -> My account -> Settings -> API keys` to retrieve your keys.
- In your Pactas backend, head to `Settings -> Payments -> Payment Providers` and edit the Paymill settings. Enter your public and private PAYMILL keys and hit 'Save'.
![PactasPaymill](https://raw.github.com/commercetools/sphere-donut/master/public/images/pactas-paymill.png)

More information about doing payments with PAYMILL at http://www.paymill.com.

### Deploy it

#### heroku

To run this SPHERE.IO example web shop on [heroku](https://www.heroku.com) just click the button:

<a href="https://heroku.com/deploy?template=https://github.com/commercetools/sphere-donut"><img src="https://www.herokucdn.com/deploy/button.png" alt="Deploy"></a>

### Develop it

- Install your favourite IDE (preferably IntelliJ, Eclipse or Netbeans).
- Generate configuration files for your chosen IDE, following [these instructions](http://www.playframework.com/documentation/2.1.x/IDE).
- Run `play` command in root project directory.
- Inside Play Shell, type `clean test` for compiling and testing it.

> This app is a [Play Framework](http://www.playframework.com/documentation/2.1.x/Home) app and uses the [SPHERE Play SDK](http://sphere.io/dev/play-sdk.html).

Have fun!
