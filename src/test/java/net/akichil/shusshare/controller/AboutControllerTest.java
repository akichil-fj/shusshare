package net.akichil.shusshare.controller;

import net.akichil.shusshare.ShusshareApplication;
import net.akichil.shusshare.test.TestWithUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@SpringBootTest(classes = ShusshareApplication.class)
@AutoConfigureMockMvc
public class AboutControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private static final String URL_PREFIX = "http://loclahost:8080/about";

    @Test
    @TestWithUser
    public void testGetAboutWhenLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("about/index"));
    }

    @Test
    @WithAnonymousUser
    public void testGetAboutWhenNotLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get(URL_PREFIX)
                        .with(SecurityMockMvcRequestPostProcessors.csrf()))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(view().name("about/index"));
    }

}
