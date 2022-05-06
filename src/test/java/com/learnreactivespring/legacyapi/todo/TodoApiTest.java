package com.learnreactivespring.legacyapi.todo;

import com.learnreactivespring.legacyapi.post.PostRestClient;
import com.learnreactivespring.legacyapi.todo.TodoRestClient;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.client.WebClient;
import com.learnreactivespring.legacyapi.todo.Todo;

import java.util.List;

@RunWith(SpringRunner.class)
@DirtiesContext
public class TodoApiTest {

    private static final String baseurl = "https://jsonplaceholder.typicode.com";
           // "http://localhost:8081";
     WebClient webClient = WebClient.create(baseurl);
    TodoRestClient todoClient = new TodoRestClient(webClient);
    PostRestClient postClient = new PostRestClient(webClient);


    @Test
    public void testRetrieveAllTodos(){
        List<Todo> todos = todoClient.getAllTodos();
        todos.forEach(System.out::print);
    }


    @Test
    public void testRetrieveAllTodosUsingReactive() throws InterruptedException {
        todoClient.getAllTodosUsingReactive().log()
               .subscribe(System.out::print);
       Thread.sleep(20000);
    }

   /* @Test
    public void testRetrieveAllIntegersUsingReactive() throws InterruptedException {
        client.getAllIntegers().log()
                .subscribe(System.out::print);
        Thread.sleep(20000);
    }*/

}
