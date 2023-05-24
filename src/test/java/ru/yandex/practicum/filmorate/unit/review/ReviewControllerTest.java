package ru.yandex.practicum.filmorate.unit.review;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;
import ru.yandex.practicum.filmorate.service.impl.ReviewServiceImpl;
import ru.yandex.practicum.filmorate.storage.dao.DAOValidator;
import ru.yandex.practicum.filmorate.storage.dao.EventStorage;
import ru.yandex.practicum.filmorate.storage.dao.impl.ReviewDbStorage;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {ReviewController.class})
@ExtendWith(SpringExtension.class)
class ReviewControllerTest {
    @Autowired
    private ReviewController reviewController;

    @MockBean
    private ReviewService reviewService;
    @MockBean
    EventStorage eventStorage;

    @Test
    void testPostReview() {
        ReviewServiceImpl service = mock(ReviewServiceImpl.class);
        Review review = new Review();
        when(service.create(Mockito.<Review>any())).thenReturn(review);
        ReviewController reviewController = new ReviewController(service);
        assertSame(review, reviewController.postReview(new Review()));
        verify(service).create(Mockito.<Review>any());
    }

    @Test
    void testPutReview() {
        ReviewDbStorage storage = mock(ReviewDbStorage.class);
        Review review = new Review();
        when(storage.update(Mockito.<Review>any())).thenReturn(review);
        DAOValidator daoValidator = mock(DAOValidator.class);
        doNothing().when(daoValidator).validateFilmBd(Mockito.<Long>any());
        doNothing().when(daoValidator).validateReviewDB(Mockito.<Long>any());
        doNothing().when(daoValidator).validateUserBd(Mockito.<Long>any());
        ReviewController reviewController = new ReviewController(
                new ReviewServiceImpl(storage, daoValidator, eventStorage));
        assertSame(review, reviewController.putReview(new Review()));
        verify(storage).update(Mockito.<Review>any());
        verify(daoValidator).validateFilmBd(Mockito.<Long>any());
        verify(daoValidator).validateReviewDB(Mockito.<Long>any());
        verify(daoValidator).validateUserBd(Mockito.<Long>any());
    }

    @Test
    void testLikeReview() throws Exception {
        doNothing().when(reviewService).likeReview(anyLong(), anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/reviews/{reviewId}/like/{userId}", 1L,
                1L);
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testLikeReview2() throws Exception {
        doNothing().when(reviewService).likeReview(anyLong(), anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/reviews/{reviewId}/like/{userId}", 1L,
                1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRemoveDislikeReview() throws Exception {
        doNothing().when(reviewService).likeReview(anyLong(), anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/reviews/{reviewId}/dislike/{userId}", 1L, 1L);
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRemoveDislikeReview2() throws Exception {
        doNothing().when(reviewService).likeReview(anyLong(), anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/reviews/{reviewId}/dislike/{userId}", 1L, 1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDeleteReview() throws Exception {
        doNothing().when(reviewService).deleteById(anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/reviews/{id}", 1);
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDislikeReview() throws Exception {
        doNothing().when(reviewService).dislikeReview(anyLong(), anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/reviews/{reviewId}/dislike/{userId}",
                1L, 1L);
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDislikeReview2() throws Exception {
        doNothing().when(reviewService).dislikeReview(anyLong(), anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/reviews/{reviewId}/dislike/{userId}",
                1L, 1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDeleteReview2() throws Exception {
        doNothing().when(reviewService).deleteById(anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/reviews/{id}", 1);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testGetReviewById() throws Exception {
        when(reviewService.findById(anyLong())).thenReturn(new Review());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/reviews/{id}", 1);
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content()
                        .string(
                                "{\"reviewId\":0,\"filmId\":0,\"userId\":0,\"content\":null,\"isPositive\":null,\"useful\":0,\"reviewDate\":null}"));
    }

    @Test
    void testGetReviewById2() throws Exception {
        when(reviewService.findByFilmId(anyLong(), anyInt())).thenReturn(new ArrayList<>());
        when(reviewService.findById(anyLong())).thenReturn(new Review());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/reviews/{id}", "", "Uri Variables");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testGetReviewsByFilmId() throws Exception {
        when(reviewService.findByFilmId(anyLong(), anyInt())).thenReturn(new ArrayList<>());
        MockHttpServletRequestBuilder getResult = MockMvcRequestBuilders.get("/reviews");
        MockHttpServletRequestBuilder paramResult = getResult.param("count", String.valueOf(1));
        MockHttpServletRequestBuilder requestBuilder = paramResult.param("filmId", String.valueOf(1));
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType("application/json"))
                .andExpect(MockMvcResultMatchers.content().string("[]"));
    }

    @Test
    void testRemoveLikeReview() throws Exception {
        doNothing().when(reviewService).dislikeReview(anyLong(), anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/reviews/{reviewId}/like/{userId}",
                1L, 1L);
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRemoveLikeReview2() throws Exception {
        doNothing().when(reviewService).dislikeReview(anyLong(), anyLong());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/reviews/{reviewId}/like/{userId}",
                1L, 1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

