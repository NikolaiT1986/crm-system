package org.nikolait.crmsystem.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.nikolait.crmsystem.dto.SellerCreateRequest;
import org.nikolait.crmsystem.dto.SellerResponse;
import org.nikolait.crmsystem.dto.SellerUpdateRequest;
import org.nikolait.crmsystem.mapper.SellerMapper;
import org.nikolait.crmsystem.model.Seller;
import org.nikolait.crmsystem.repository.SellerRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SellerServiceImplUnitTest {

    @Mock
    private SellerRepository sellerRepository;

    @Mock
    private SellerMapper sellerMapper;

    @InjectMocks
    private SellerServiceImpl sellerService;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    // --------------------------------------------
    //            getAll()
    // --------------------------------------------
    @Test
    void getAll_returnsPage() {
        Seller seller = new Seller();
        SellerResponse response = new SellerResponse(1L, "John", "mail", LocalDateTime.now());

        when(sellerRepository.findAllByDeletedFalse(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(seller)));

        when(sellerMapper.toResponse(seller))
                .thenReturn(response);

        Page<SellerResponse> page = sellerService.getAll(Pageable.ofSize(10));

        assertEquals(1, page.getTotalElements());
        assertEquals("John", page.getContent().getFirst().name());
    }

    // --------------------------------------------
    //             getById()
    // --------------------------------------------
    @Test
    void getById_returnsSeller() {
        Seller seller = new Seller();
        seller.setId(10L);

        SellerResponse response = new SellerResponse(10L, "John", "info", LocalDateTime.now());

        when(sellerRepository.findByIdAndDeletedFalse(10L))
                .thenReturn(Optional.of(seller));

        when(sellerMapper.toResponse(seller))
                .thenReturn(response);

        SellerResponse result = sellerService.getById(10L);

        assertEquals(10L, result.id());
        assertEquals("John", result.name());
    }

    @Test
    void getById_throwsException_whenNotFound() {
        when(sellerRepository.findByIdAndDeletedFalse(99L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> sellerService.getById(99L));
    }

    // --------------------------------------------
    //              create()
    // --------------------------------------------
    @Test
    void create_savesSeller() {
        SellerCreateRequest req = new SellerCreateRequest("John", "mail");

        Seller seller = new Seller();
        Seller saved = new Seller();
        saved.setId(1L);

        SellerResponse response = new SellerResponse(1L, "John", "mail", LocalDateTime.now());

        when(sellerMapper.toEntity(req)).thenReturn(seller);
        when(sellerRepository.save(seller)).thenReturn(saved);
        when(sellerMapper.toResponse(saved)).thenReturn(response);

        SellerResponse result = sellerService.create(req);

        assertEquals(1L, result.id());
    }

    // --------------------------------------------
    //              update()
    // --------------------------------------------
    @Test
    void update_updatesFields() {
        Seller existing = new Seller();
        existing.setId(5L);

        SellerUpdateRequest req = new SellerUpdateRequest("NewName", "NewInfo");

        SellerResponse response = new SellerResponse(
                5L, "NewName", "NewInfo", LocalDateTime.now()
        );

        when(sellerRepository.findByIdAndDeletedFalse(5L)).thenReturn(Optional.of(existing));
        when(sellerRepository.save(existing)).thenReturn(existing);
        when(sellerMapper.toResponse(existing)).thenReturn(response);

        SellerResponse result = sellerService.update(5L, req);

        verify(sellerMapper).patchFromRequest(req, existing);

        assertEquals("NewName", result.name());
    }

    @Test
    void update_throwsException_whenNotFound() {
        when(sellerRepository.findByIdAndDeletedFalse(123L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> sellerService.update(123L, new SellerUpdateRequest("n", "i")));
    }

    // --------------------------------------------
    //              delete()
    // --------------------------------------------
    @Test
    void delete_marksAsDeleted() {

        Seller seller = new Seller();
        seller.setId(7L);
        seller.setDeleted(false);

        when(sellerRepository.findByIdAndDeletedFalse(7L))
                .thenReturn(Optional.of(seller));

        sellerService.delete(7L);

        ArgumentCaptor<Seller> captor = ArgumentCaptor.forClass(Seller.class);
        verify(sellerRepository).save(captor.capture());

        assertTrue(captor.getValue().isDeleted());
    }

    @Test
    void delete_throwsException_whenNotFound() {
        when(sellerRepository.findByIdAndDeletedFalse(11L))
                .thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> sellerService.delete(11L));
    }
}