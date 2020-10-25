package com.jab.watcher.model.rate;

import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "limit",
    "used",
    "remaining",
    "reset"
})
public class Graphql {

    @JsonProperty("limit")
    private Integer limit;
    @JsonProperty("used")
    private Integer used;
    @JsonProperty("remaining")
    private Integer remaining;
    @JsonProperty("reset")
    private Integer reset;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Graphql() {
    }

    /**
     *
     * @param limit
     * @param reset
     * @param used
     * @param remaining
     */
    public Graphql(Integer limit, Integer used, Integer remaining, Integer reset) {
        super();
        this.limit = limit;
        this.used = used;
        this.remaining = remaining;
        this.reset = reset;
    }

    @JsonProperty("limit")
    public Integer getLimit() {
        return limit;
    }

    @JsonProperty("limit")
    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    @JsonProperty("used")
    public Integer getUsed() {
        return used;
    }

    @JsonProperty("used")
    public void setUsed(Integer used) {
        this.used = used;
    }

    @JsonProperty("remaining")
    public Integer getRemaining() {
        return remaining;
    }

    @JsonProperty("remaining")
    public void setRemaining(Integer remaining) {
        this.remaining = remaining;
    }

    @JsonProperty("reset")
    public Integer getReset() {
        return reset;
    }

    @JsonProperty("reset")
    public void setReset(Integer reset) {
        this.reset = reset;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
