package ru.yandex.practicum.filmorate.unit.likeReview;

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
import ru.yandex.practicum.filmorate.controller.LikeReviewController;
import ru.yandex.practicum.filmorate.storage.LikeReviewStorage;

import static org.mockito.Mockito.doNothing;

@ContextConfiguration(classes = {LikeReviewController.class})
@ExtendWith(SpringExtension.class)
class LikeReviewControllerTest {
    @Autowired
    private LikeReviewController likeReviewController;

    @MockBean
    private LikeReviewStorage likeReviewStorage;

    @Test
    void testLikeReview() throws Exception {
        doNothing().when(likeReviewStorage).likeReview(Mockito.<Long>any(), Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/reviews/{reviewId}/like/{userId}", 1L,
                1L);
        MockMvcBuilders.standaloneSetup(likeReviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testLikeReview2() throws Exception {
        doNothing().when(likeReviewStorage).likeReview(Mockito.<Long>any(), Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/reviews/{reviewId}/like/{userId}", 1L,
                1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(likeReviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRemoveDislikeReview() throws Exception {
        doNothing().when(likeReviewStorage).likeReview(Mockito.<Long>any(), Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/reviews/{reviewId}/dislike/{userId}", 1L, 1L);
        MockMvcBuilders.standaloneSetup(likeReviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRemoveDislikeReview2() throws Exception {
        doNothing().when(likeReviewStorage).likeReview(Mockito.<Long>any(), Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .delete("/reviews/{reviewId}/dislike/{userId}", 1L, 1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(likeReviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDislikeReview() throws Exception {
        doNothing().when(likeReviewStorage).dislikeReview(Mockito.<Long>any(), Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/reviews/{reviewId}/dislike/{userId}",
                1L, 1L);
        MockMvcBuilders.standaloneSetup(likeReviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testDislikeReview2() throws Exception {
        doNothing().when(likeReviewStorage).dislikeReview(Mockito.<Long>any(), Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.put("/reviews/{reviewId}/dislike/{userId}",
                1L, 1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(likeReviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRemoveLikeReview() throws Exception {
        doNothing().when(likeReviewStorage).dislikeReview(Mockito.<Long>any(), Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/reviews/{reviewId}/like/{userId}",
                1L, 1L);
        MockMvcBuilders.standaloneSetup(likeReviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testRemoveLikeReview2() throws Exception {
        doNothing().when(likeReviewStorage).dislikeReview(Mockito.<Long>any(), Mockito.<Long>any());
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.delete("/reviews/{reviewId}/like/{userId}",
                1L, 1L);
        requestBuilder.characterEncoding("Encoding");
        MockMvcBuilders.standaloneSetup(likeReviewController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}

