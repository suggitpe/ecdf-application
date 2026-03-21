package org.acmebank.people.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Optional;

@ControllerAdvice
public class GlobalModelAdvice {

    private final BuildProperties buildProperties;

    @Autowired
    public GlobalModelAdvice(Optional<BuildProperties> buildProperties) {
        this.buildProperties = buildProperties.orElse(null);
    }

    @ModelAttribute("requestURI")
    public String requestURI(final HttpServletRequest request) {
        return request.getRequestURI();
    }

    @ModelAttribute("appVersion")
    public String appVersion() {
        return buildProperties != null ? buildProperties.getVersion() : "dev-local";
    }
}
