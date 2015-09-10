package handler;

import play.Configuration;
import play.Environment;
import play.Logger;
import play.api.OptionalSourceMapper;
import play.api.UsefulException;
import play.api.routing.Router;
import play.http.DefaultHttpErrorHandler;
import play.libs.F;
import play.mvc.Http;
import play.mvc.Result;
import play.mvc.Results;

import javax.inject.Inject;
import javax.inject.Provider;

public class DonutHttpErrorHandler extends DefaultHttpErrorHandler {

    private static final Logger.ALogger LOG = Logger.of(DonutHttpErrorHandler.class);

    @Inject
    public DonutHttpErrorHandler(final Configuration configuration, final Environment environment,
                                 final OptionalSourceMapper optionalSourceMapper, final Provider<Router> provider) {
        super(configuration, environment, optionalSourceMapper, provider);
    }

    @Override
    protected F.Promise<Result> onBadRequest(final Http.RequestHeader requestHeader, final String message) {
        LOG.debug("Handle onBadRequest(), {}, {}", requestHeader, message);
        return F.Promise.<Result>pure(Results.badRequest("Bad request."));
    }

    @Override
    public F.Promise<Result> onClientError(final Http.RequestHeader requestHeader, final int i, final String message) {
        LOG.debug("Handle onClientError(), {}, {}", requestHeader, message);
        return super.onClientError(requestHeader, i, message);
    }

    @Override
    protected F.Promise<Result> onDevServerError(final Http.RequestHeader requestHeader, final UsefulException e) {
        LOG.debug("Handle onDevServerError(), {}, {}", requestHeader, e);
        return F.Promise.<Result>pure(Results.internalServerError("A [DEV]-Server error occurred: " + e.getMessage()));
    }

    @Override
    protected F.Promise<Result> onForbidden(final Http.RequestHeader requestHeader, final String message) {
        LOG.debug("Handle onForbidden(), {}, {}", requestHeader, message);
        return F.Promise.<Result>pure(Results.forbidden("You're not allowed to access this resource."));
    }

    @Override
    protected F.Promise<Result> onNotFound(final Http.RequestHeader requestHeader, final String message) {
        LOG.debug("Handle onNotFound(), {}, {}", requestHeader, message);
        return F.Promise.<Result>pure(Results.notFound("Resource not found.."));
    }

    @Override
    protected F.Promise<Result> onProdServerError(final Http.RequestHeader requestHeader, final UsefulException e) {
        LOG.debug("Handle onProdServerError(), {}, {}", requestHeader, e);
        return F.Promise.<Result>pure(Results.internalServerError("A [PROD]-Server error occurred: " + e.getMessage()));
    }

    @Override
    public F.Promise<Result> onServerError(final Http.RequestHeader requestHeader, final Throwable throwable) {
        LOG.debug("Handle onServerError(), {}, {}", requestHeader, throwable);
        return super.onServerError(requestHeader, throwable);
    }
}
