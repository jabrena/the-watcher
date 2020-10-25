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
    "core",
    "search",
    "graphql",
    "integration_manifest",
    "source_import",
    "code_scanning_upload"
})
public class Resources {

    @JsonProperty("core")
    private Core core;
    @JsonProperty("search")
    private Search search;
    @JsonProperty("graphql")
    private Graphql graphql;
    @JsonProperty("integration_manifest")
    private IntegrationManifest integrationManifest;
    @JsonProperty("source_import")
    private SourceImport sourceImport;
    @JsonProperty("code_scanning_upload")
    private CodeScanningUpload codeScanningUpload;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    /**
     * No args constructor for use in serialization
     *
     */
    public Resources() {
    }

    /**
     *
     * @param core
     * @param sourceImport
     * @param search
     * @param integrationManifest
     * @param graphql
     * @param codeScanningUpload
     */
    public Resources(Core core, Search search, Graphql graphql, IntegrationManifest integrationManifest, SourceImport sourceImport, CodeScanningUpload codeScanningUpload) {
        super();
        this.core = core;
        this.search = search;
        this.graphql = graphql;
        this.integrationManifest = integrationManifest;
        this.sourceImport = sourceImport;
        this.codeScanningUpload = codeScanningUpload;
    }

    @JsonProperty("core")
    public Core getCore() {
        return core;
    }

    @JsonProperty("core")
    public void setCore(Core core) {
        this.core = core;
    }

    @JsonProperty("search")
    public Search getSearch() {
        return search;
    }

    @JsonProperty("search")
    public void setSearch(Search search) {
        this.search = search;
    }

    @JsonProperty("graphql")
    public Graphql getGraphql() {
        return graphql;
    }

    @JsonProperty("graphql")
    public void setGraphql(Graphql graphql) {
        this.graphql = graphql;
    }

    @JsonProperty("integration_manifest")
    public IntegrationManifest getIntegrationManifest() {
        return integrationManifest;
    }

    @JsonProperty("integration_manifest")
    public void setIntegrationManifest(IntegrationManifest integrationManifest) {
        this.integrationManifest = integrationManifest;
    }

    @JsonProperty("source_import")
    public SourceImport getSourceImport() {
        return sourceImport;
    }

    @JsonProperty("source_import")
    public void setSourceImport(SourceImport sourceImport) {
        this.sourceImport = sourceImport;
    }

    @JsonProperty("code_scanning_upload")
    public CodeScanningUpload getCodeScanningUpload() {
        return codeScanningUpload;
    }

    @JsonProperty("code_scanning_upload")
    public void setCodeScanningUpload(CodeScanningUpload codeScanningUpload) {
        this.codeScanningUpload = codeScanningUpload;
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
