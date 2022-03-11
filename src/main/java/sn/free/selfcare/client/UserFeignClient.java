package sn.free.selfcare.client;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import sn.free.selfcare.service.dto.AdminClientDTO;

import java.util.Map;

@AuthorizedFeignClient(name = "selfcareuaa")
public interface UserFeignClient {

    @RequestMapping(method = RequestMethod.DELETE, value = "/api/users/client/{clientId}")
    void deleteClientUsers(@PathVariable Long clientId);

    @RequestMapping(method = RequestMethod.POST, value = "/api/users/createAdminClient")
    Map<String, String> createAdminClientUser(AdminClientDTO user);
}
