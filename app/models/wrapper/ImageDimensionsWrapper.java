package models.wrapper;

import com.fasterxml.jackson.annotation.JsonProperty;

import static java.util.Objects.requireNonNull;

public class ImageDimensionsWrapper {

    private final Integer width;
    private final Integer height;

    public ImageDimensionsWrapper(@JsonProperty("w") final Integer width, @JsonProperty("h") final Integer height) {
        this.width = requireNonNull(width);
        this.height = requireNonNull(height);
    }

    public Integer getWidth() {
        return width;
    }

    public Integer getHeight() {
        return height;
    }
}
