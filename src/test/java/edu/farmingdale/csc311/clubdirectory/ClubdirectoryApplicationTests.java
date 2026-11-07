package edu.farmingdale.csc311.clubdirectory;

import edu.farmingdale.csc311.clubdirectory.club.ClubRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

// Keep automated tests separate from the saved clubs in data/clubs-db.
@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:derby:memory:clubs-test-db;create=true",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc
@Transactional
class ClubdirectoryApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ClubRepository repository;

    @Test
    void loadsThreeStarterClubs() throws Exception {
        mockMvc.perform(get("/api/clubs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].name").value("Computer Science Club"))
                .andExpect(jsonPath("$[1].name").value("Chess Club"))
                .andExpect(jsonPath("$[2].name").value("Running Club"));
    }

    @Test
    void filtersCategoriesWithoutCaseSensitivity() throws Exception {
        for (String category : new String[]{"Sports", "sPoRtS"}) {
            mockMvc.perform(get("/api/clubs").param("category", category))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].name").value("Running Club"));
        }
        mockMvc.perform(get("/api/clubs").param("category", "Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    void findsExistingClubAndReturns404ForMissingClub() throws Exception {
        Long id = repository.findByCategoryIgnoreCase("Sports").getFirst().getId();
        mockMvc.perform(get("/api/clubs/" + id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Running Club"));

        mockMvc.perform(get("/api/clubs/999999"))
                .andExpect(status().isNotFound())
                .andExpect(result -> assertEquals("No club with id 999999",
                        result.getResponse().getErrorMessage()));
    }

    @Test
    void createsClubWithGeneratedId() throws Exception {
        mockMvc.perform(post("/api/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Robotics Club","category":"Academic",
                                 "meetingDay":"Thursday","room":"Lupton Hall 210"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.name").value("Robotics Club"));
        assertEquals(4, repository.count());
    }

    @Test
    void rejectsMissingAndBlankRequiredFields() throws Exception {
        String[] invalidClubs = {
                "{}",
                "{\"category\":\"Academic\",\"meetingDay\":\"Monday\"}",
                "{\"name\":\"Test Club\",\"meetingDay\":\"Monday\"}",
                "{\"name\":\"Test Club\",\"category\":\"Academic\"}",
                "{\"name\":\"  \",\"category\":\"  \",\"meetingDay\":\"  \"}"
        };
        for (String club : invalidClubs) {
            mockMvc.perform(post("/api/clubs")
                            .contentType(MediaType.APPLICATION_JSON).content(club))
                    .andExpect(status().isBadRequest());
        }
        assertEquals(3, repository.count());
    }

    @Test
    void rejectsEachFieldOverItsMaximumLength() throws Exception {
        String[] fields = {"name", "category", "meetingDay", "room"};
        String[] originalValues = {"Test Club", "Academic", "Monday", "Hall"};
        int[] limits = {60, 30, 20, 40};
        for (int i = 0; i < fields.length; i++) {
            String club = "{\"name\":\"Test Club\",\"category\":\"Academic\","
                    + "\"meetingDay\":\"Monday\",\"room\":\"Hall\"}";
            club = club.replace(originalValues[i], "x".repeat(limits[i] + 1));
            mockMvc.perform(post("/api/clubs")
                            .contentType(MediaType.APPLICATION_JSON).content(club))
                    .andExpect(status().isBadRequest());
        }
        assertEquals(3, repository.count());
    }

    @Test
    void acceptsMaximumLengthsAndOptionalRoom() throws Exception {
        String club = "{\"name\":\"" + "n".repeat(60)
                + "\",\"category\":\"" + "c".repeat(30)
                + "\",\"meetingDay\":\"" + "d".repeat(20)
                + "\",\"room\":\"" + "r".repeat(40) + "\"}";
        mockMvc.perform(post("/api/clubs")
                        .contentType(MediaType.APPLICATION_JSON).content(club))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/clubs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Book Club","category":"Arts","meetingDay":"Monday"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.room").doesNotExist());
    }
}
