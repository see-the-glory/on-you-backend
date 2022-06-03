package stg.onyou.controller;


import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;
import stg.onyou.model.entity.Organization;
import stg.onyou.model.network.Header;
import stg.onyou.model.network.response.OrganizationResponse;
import stg.onyou.service.OrganizationService;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = {"Organization API Controller"})
@RestController
@Slf4j
@RequiredArgsConstructor
public class OrganizationController {

    private final OrganizationService organizationService;

    @GetMapping("/api/organizations")
    public Header<List<OrganizationResponse>> selectOrganizationList() {
        List<Organization> organizations = organizationService.getOrganizations();
        List<OrganizationResponse> resultList = organizations.stream().map(o -> new OrganizationResponse(o.getName(), o.getPastorName(), o.getAddress()))
                .collect(Collectors.toList());
        return Header.OK(resultList);

    }


}
