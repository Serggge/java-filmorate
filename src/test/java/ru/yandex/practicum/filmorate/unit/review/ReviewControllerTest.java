package ru.yandex.practicum.filmorate.unit.review;

import static org.mockito.Mockito.anyInt;
import static org.mockito.Mockito.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.yandex.practicum.filmorate.controller.ReviewController;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

@ContextConfiguration(classes = {ReviewController.class})
@ExtendWith(SpringExtension.class)
class ReviewControllerTest {
    @Autowired
    private ReviewController reviewController;

    @MockBean
    private ReviewService reviewService;

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
        MockHttpServletRequestBuilder putResult = MockMvcRequestBuilders.put("/reviews/{reviewId}/like/{userId}", 1L, 1L);
        putResult.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(putResult)
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
        MockHttpServletRequestBuilder deleteResult = MockMvcRequestBuilders.delete("/reviews/{reviewId}/dislike/{userId}",
                1L, 1L);
        deleteResult.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(deleteResult)
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
        MockHttpServletRequestBuilder putResult = MockMvcRequestBuilders.put("/reviews/{reviewId}/dislike/{userId}", 1L,
                1L);
        putResult.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(putResult)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDeleteReview2() throws Exception {
        doNothing().when(reviewService).deleteById(anyLong());
        MockHttpServletRequestBuilder deleteResult = MockMvcRequestBuilders.delete("/reviews/{id}", 1);
        deleteResult.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(deleteResult)
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
    void testPostReview() throws Exception {
        when(reviewService.findByFilmId(anyLong(), anyInt())).thenReturn(new ArrayList<>());

        Review review = new Review();
        review.setContent("Not all who wander are lost");
        review.setFilmId(1L);
        review.setIsPositive(true);
        review.setReviewDate(null);
        review.setReviewId(1L);
        review.setUseful(1);
        review.setUserId(1L);
        String content = (new ObjectMapper()).writeValueAsString(review);
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content);
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
        MockHttpServletRequestBuilder deleteResult = MockMvcRequestBuilders.delete("/reviews/{reviewId}/like/{userId}",
                1L, 1L);
        deleteResult.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(reviewController)
                .build()
                .perform(deleteResult)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

}

