package io.tjf.releasenotes.azure.service;

import java.util.Arrays;
import java.util.stream.Stream;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import io.tjf.releasenotes.properties.ApplicationProperties;

/**
 * Azure REST API interactive class.
 * 
 * @author Rubens dos Santos Filho
 */
@Component
public class AzureService {

	protected static final String BASE_URL = "https://dev.azure.com/{organization}/{project}/_apis/git/repositories/{repositoryId}";
	protected static final String VERSION_URI = "api-version=5.2-preview.1";

	private final RestTemplate restTemplate;
	private final Object[] urlConfig;

	public AzureService(RestTemplateBuilder builder, ApplicationProperties properties) {
		String organization = properties.getAzure().getOrganization();
		String project = properties.getAzure().getProject();
		String repository = properties.getAzure().getRepository();
		this.urlConfig = new Object[] { organization, project, repository };

		String username = properties.getAzure().getUsername();
		String password = properties.getAzure().getPassword();
		builder = builder.basicAuthentication(username, password);
		this.restTemplate = builder.build();
	}

	public <T> T get(Class<T> returnType, String uri, Object... uriVariables) {
		String url = BASE_URL + "/" + uri;
		url += url.indexOf("?") >= 0 ? "&" : "?";
		url += VERSION_URI;
		Object[] args = Stream.concat(Arrays.stream(urlConfig), Arrays.stream(uriVariables)).toArray();
		return this.restTemplate.getForObject(url, returnType, args);
	}

}
