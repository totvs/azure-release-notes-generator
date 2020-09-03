package io.tjf.releasenotes.azure.service;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

import io.tjf.releasenotes.properties.ReleaseNotesProperties;

/**
 * Azure REST API interactive class.
 * 
 * @author Rubens dos Santos Filho
 */
public abstract class AzureService {

	protected static final String AZURE_URL = "https://dev.azure.com/{organization}/{project}/_apis/git/repositories/{repositoryId}";
	private static final String API_VERSION = "api-version=6.0-preview";

	private final RestTemplate restTemplate;
	private final Object[] uriConfigVariables;

	public AzureService(final RestTemplateBuilder builder, final ReleaseNotesProperties properties) {
		var organization = properties.getAzure().getOrganization();
		var project = properties.getAzure().getProject();
		var repositoryId = properties.getAzure().getRepository();
		uriConfigVariables = new String[] { organization, project, repositoryId };

		var username = properties.getAzure().getUsername();
		var password = properties.getAzure().getPassword();
		restTemplate = builder.basicAuthentication(username, password).build();
	}

	public <T> T get(final Class<T> returnClass, final String apiUri, Object... uriVariables) {
		var url = AZURE_URL + "/" + apiUri + (apiUri.indexOf('?') >= 0 ? "&" : "?") + API_VERSION;
		uriVariables = Stream.concat(Arrays.stream(uriConfigVariables), Arrays.stream(uriVariables)).toArray();
		return restTemplate.getForObject(url, returnClass, uriVariables);
	}

}
