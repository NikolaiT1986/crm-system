package org.nikolait.crmsystem.mapper;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.nikolait.crmsystem.dto.SellerCreateRequest;
import org.nikolait.crmsystem.dto.SellerResponse;
import org.nikolait.crmsystem.dto.SellerUpdateRequest;
import org.nikolait.crmsystem.model.Seller;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SellerMapperUnitTest {

    private final SellerMapper sellerMapper = Mappers.getMapper(SellerMapper.class);

    // --------------------------------------------
    //            toResponse()
    // --------------------------------------------
    @Test
    void toResponse_mapsSellerToSellerResponse() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("John");
        seller.setContactInfo("contact@mail.com");
        seller.setRegistrationDate(LocalDateTime.of(2023, 1, 1, 12, 0));

        SellerResponse response = sellerMapper.toResponse(seller);

        assertEquals(1L, response.id());
        assertEquals("John", response.name());
        assertEquals("contact@mail.com", response.contactInfo());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), response.registrationDate());
    }

    // --------------------------------------------
    //            toEntity()
    // --------------------------------------------
    @Test
    void toEntity_mapsSellerCreateRequestToSeller() {
        SellerCreateRequest request = new SellerCreateRequest("John", "contact@mail.com");

        Seller seller = sellerMapper.toEntity(request);

        assertNotNull(seller);
        assertEquals("John", seller.getName());
        assertEquals("contact@mail.com", seller.getContactInfo());
    }

    // --------------------------------------------
    //            patchFromRequest()
    // --------------------------------------------
    @Test
    void patchFromRequest_updatesOnlyProvidedFields() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("John");
        seller.setContactInfo("old-contact@mail.com");

        SellerUpdateRequest request = new SellerUpdateRequest("NewName", null); // only name should be updated

        sellerMapper.patchFromRequest(request, seller);

        assertEquals("NewName", seller.getName());  // updated
        assertEquals("old-contact@mail.com", seller.getContactInfo());  // not updated
    }

    @Test
    void patchFromRequest_doesNotUpdateWhenFieldIsNull() {
        Seller seller = new Seller();
        seller.setId(1L);
        seller.setName("John");
        seller.setContactInfo("old-contact@mail.com");

        SellerUpdateRequest request = new SellerUpdateRequest(null, "new-contact@mail.com");

        sellerMapper.patchFromRequest(request, seller);

        assertEquals("John", seller.getName());  // not updated
        assertEquals("new-contact@mail.com", seller.getContactInfo());  // updated
    }
}