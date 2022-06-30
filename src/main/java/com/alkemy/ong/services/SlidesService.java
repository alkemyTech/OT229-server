package com.alkemy.ong.services;

import com.alkemy.ong.dto.SlidesEntityDTO;

import java.util.List;

public interface SlidesService {

    List<SlidesEntityDTO> findByOrganization(String organizationId);

}
