package it.gov.pagopa.arc.service;

import it.gov.pagopa.arc.model.generated.OrganizationsListDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

@Slf4j
@Service
public class PaymentNoticeSpontaneousServiceImpl implements PaymentNoticeSpontaneousService{

    private final String pathOrganizationsMock;

    private final JsonMapper jsonMapper;

    public PaymentNoticeSpontaneousServiceImpl(@Value("${spontaneous-mock-paths.organizationList}")String pathOrganizationsMock, JsonMapper jsonMapper) {
        this.pathOrganizationsMock = pathOrganizationsMock;
        this.jsonMapper = jsonMapper;
    }

    @Override
    public OrganizationsListDTO retrieveOrganizations(String userId) {
        OrganizationsListDTO organizationsListDTO = OrganizationsListDTO.builder().organizations(new ArrayList<>()).build();

        log.info("[GET_SPONTANEOUS_ORGANIZATIONS_LIST] User {} initialized spontaneous process and client requested spontaneous organizations list", userId);

        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(pathOrganizationsMock)) {
            if(inputStream == null){
                log.info("File with path [{}] not found", pathOrganizationsMock);
                return organizationsListDTO;
            }
            return jsonMapper.readValue(inputStream, OrganizationsListDTO.class);
        } catch (JacksonException | IOException | NullPointerException e) {
            log.info("Error reading the file", e);
            return organizationsListDTO;
        }
    }
}
