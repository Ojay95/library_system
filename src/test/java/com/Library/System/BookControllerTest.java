package com.Library.System;

import com.Library.System.controller.BookController;
import com.Library.System.model.Book;
import com.Library.System.service.BookService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class BookControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BookService bookService;

    @InjectMocks
    private BookController bookController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(bookController).build();
    }

    @Test
    void getAllBooks() throws Exception {
        List<Book> books = Arrays.asList(
                new Book(1L, "Book Title 1", "Author 1", "Genre 1", LocalDate.of(2022, 1, 1), true, "Edition 1"),
                new Book(2L, "Book Title 2", "Author 2", "Genre 2", LocalDate.of(2022, 2, 1), true, "Edition 2")
        );

        when(bookService.getAllBooks()).thenReturn(books);

        mockMvc.perform(get("/api/v1/books/"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    void getBookById() throws Exception {
        Book book = new Book(1L, "Book Title", "Author", "Genre", LocalDate.of(2022, 1, 1), true, "Edition 1");

        when(bookService.getBookById(1L)).thenReturn(Optional.of(book));

        mockMvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Book Title"));
    }

    @Test
    void getBookById_NotFound() throws Exception {
        when(bookService.getBookById(anyLong())).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getBooksByGenre() throws Exception {
        List<Book> books = Arrays.asList(
                new Book(1L, "Book Title", "Author", "Fiction", LocalDate.of(2022, 1, 1), true, "Summary 1")
        );

        when(bookService.getBooksByGenre("Fiction")).thenReturn(books);

        mockMvc.perform(get("/api/v1/books/genre/Fiction"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(1)));
    }



    @Test
    void addBook() throws Exception {
        Book book = new Book(1L, "New Book Title", "New Author", "New Genre", LocalDate.of(2023, 1, 1), true, "Edition 1");

        when(bookService.addBook(any(Book.class))).thenReturn(book);

        mockMvc.perform(post("/api/v1/books/create") // Ensure this matches the controller's @PostMapping("/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"New Book Title\",\"author\":\"New Author\",\"genre\":\"New Genre\",\"publicationDate\":\"2023-01-01\",\"availabilityStatus\":true,\"summary\":\"New Summary\"}"))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("New Book Title"));
    }



    @Test
    void updateBook() throws Exception {
        Book existingBook = new Book(1L, "Old Title", "Old Author", "Old Genre", LocalDate.of(2022, 1, 1), true, "Old Summary");
        Book updatedBook = new Book(1L, "Updated Title", "Updated Author", "Updated Genre", LocalDate.of(2023, 1, 1), false, "Updated Summary");

        when(bookService.updateBook(anyLong(), any(Book.class))).thenReturn(updatedBook);
        when(bookService.getBookById(1L)).thenReturn(Optional.of(existingBook));

        mockMvc.perform(put("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\":\"Updated Title\",\"author\":\"Updated Author\",\"genre\":\"Updated Genre\",\"publicationDate\":\"2023-01-01\",\"availabilityStatus\":false,\"edition\":\"Updated Edition\",\"summary\":\"Updated Summary\"}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.title").value("Updated Title"));
    }

    @Test
    void deleteBook() throws Exception {
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/v1/books/1"))
                .andExpect(status().isNoContent());

        verify(bookService, times(1)).deleteBook(1L);
    }

    @Test
    void deleteBook_NotFound() throws Exception {
        doThrow(new RuntimeException("Book not found with id 1")).when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/books/1"))
                .andExpect(status().isNotFound());
    }
}
