package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public class ImageWrapper {

    private final String url;
    private final ImageDimensionsWrapper dimensions;

    public ImageWrapper(@JsonProperty("url") final String url,
                        @JsonProperty("dimensions") final ImageDimensionsWrapper dimensions) {
        this.url = requireNonNull(url);
        this.dimensions = requireNonNull(dimensions);
    }

    public String getUrl() {
        return url;
    }

    public ImageDimensionsWrapper getDimensions() {
        return dimensions;
    }
}
