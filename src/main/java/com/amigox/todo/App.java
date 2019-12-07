package com.amigox.todo;


import com.amigox.todo.Entity.*;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import com.amigox.todo.Util.HashUtil;
import org.jetbrains.annotations.NotNull;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.nio.charset.Charset;
import java.util.*;

import static spark.Spark.*;
import static spark.debug.DebugScreen.enableDebugScreen;

/**
 * Spark Web Framework & REST API!
 *
 * @link http://www.jsonschema2pojo.org/
 */
public class App {
    public static void main(String[] args) {
        staticFiles.location("/public");
        port(8080);
        // DbUtil.getSessionFactory().getCurrentSession();
        get("/", (req, res) -> {

            var model = Map.of("name", "TODO", "todos", new TodoDao().getAll());

            return  render(model, "index.hbs");

        });

        get("/login", (req, res) -> render(new HashMap<>(), "login.hbs"));
        get("/register", (req, res) -> render(new HashMap<>(), "register.hbs"));

        post("/login", (req, res) -> {
            Map<String, String> errors = new HashMap<>();
            var fields = getPreparedFields(req.body());

            var username = fields.get("username");
            var password = fields.get("password");

            if (username.isEmpty() || password.isEmpty()) {
                errors.put("error", "Empty username and/or password!");
                res.redirect("/login");
                return null;
            }

            var myUser = new UserDao().findByUsername(username);
            if (myUser.isEmpty()) {
                res.header("error", "User not found!");
                res.redirect("/login");
                return null;
            }

            // compare incoming password's hash to the existing user password
            if (!HashUtil.passwordVerify(password, myUser.get().getPassword())) {
                res.header("error", "Passwords do not match!");
                res.redirect("/login");
                return null;
            }

            req.session(true).attribute("user", username);
            res.redirect("/todos/" + username);
            return null;
        });

        post("/register", (req, res) -> {
            List<NameValuePair> pairs = URLEncodedUtils.parse(req.body(), Charset.defaultCharset());
            Map<String, String> params = toMap(pairs);

            // Validate passwords match
            if (!params.get("password").equals(params.get("confirm_password"))) {
                res.header("Error", "Passwords do not match!");
                res.redirect("/register");
            }

            // TODO: validate user with email not exists

            String existingEmail = params.get("email");
            Optional<User> existingUser = new UserDao().findByEmail(existingEmail);
            if (existingUser.isPresent()){
                res.header("Error", "User exists");
                res.redirect("/register");
            }




            User newUser = new User();
            String[] fullName = params.get("fullName").split(" ");
            newUser.setFirstName(fullName[0]);
            newUser.setLastName(fullName[1]);
            newUser.setEmail(params.get("email"));
            newUser.setUsername(params.get("email").split("@")[0]);
            newUser.setPassword(HashUtil.passwordHash(params.get("password")));



            new UserDao().save(newUser);
            res.redirect("/login");
            return null;
        });

        get("/todos", (req, res) -> {

//                List<Todo> todoList = new TodoDao().getAll();
//                System.out.println(todoList.toString());


                var model = Map.of("name", "All Todos", "todos", new TodoDao().getAll());
                return render(model, "todos.hbs");

        });

        get("/todos/:username", (req, res) -> {
            String username = req.params(":username");
            Optional<User> thisUser = new UserDao().findByUsername(username);
            String name = thisUser.get().getFirstName().toUpperCase();

            var model = Map.of("name", name, "todos", new TodoDao().getAllByUsername(username));
            return render(model, "todos.hbs");

        });

        get("/addTodo", (req, res) -> {

            var allLabels = new LabelDao().getAll();
            var model = Map.of("name", "TODO", "labels", allLabels);



            return  render(model, "addTodo.hbs");

        });

        post("/addTodo", ((req, res) -> {
            List<NameValuePair> pairs = URLEncodedUtils.parse(req.body(), Charset.defaultCharset());
            Map<String, String> params = toMap(pairs);

            String thisUsername = req.session().attribute("user");
            Optional<User> myUser = new UserDao().findByUsername(thisUsername);

            if (!myUser.isEmpty()) {
                Todo newTodo = new Todo();
                newTodo.setTitle(params.get("todoTitle"));
                newTodo.setNote(params.get("todoNote"));
                newTodo.setPriority(Integer.parseInt(params.get("priority")));
                newTodo.setUser(myUser.get());
//                newTodo.setLabels(params.get("labels"));

                new TodoDao().save(newTodo);
                res.redirect("/todos");

            }

           return null;

        }));
//TODO
//        get("/updateTodo/:id", (req, res) -> {
//
//            var todoId = Integer.parseInt(":id");
//            var allLabels = new LabelDao().getAll();
//            Optional<Todo> todo = new TodoDao().findByUsername(todoId);
//
//            if (!todo.isEmpty()) {
//                var model = Map.of("name", "TODO", "labels", allLabels, "todo", todo);
//
//                return render(model, "updateTodo.hbs");
//            }
//            return null;
//
//        });

        get("/addLabel",(req, res) -> render(new HashMap<>(), "addLabel.hbs"));

        post("/addLabel", (req, res) -> {
            List<NameValuePair> pairs = URLEncodedUtils.parse(req.body(), Charset.defaultCharset());
            Map<String, String> params = toMap(pairs);

            Label newLabel = new Label();
            newLabel.setName(params.get("labelName"));
            newLabel.setSlug(params.get("labelSlug"));

            new LabelDao().save(newLabel);
            res.redirect("/todos");
            return null;
        });



        


        enableDebugScreen();
    }

    private static String render(Map<String, Object> model, String templatePath) {
        return new HandlebarsTemplateEngine().render(new ModelAndView(model, templatePath));
    }

    private static Map<String, String> toMap(List<NameValuePair> pairs) {
        Map<String, String> map = new HashMap<>();
        for (NameValuePair pair : pairs) {
            map.put(pair.getName(), pair.getValue());
        }
        return map;
    }

    private static Map<String, String> getPreparedFields(String request) {
        Map<String, String> fields = new HashMap<>();
        String[] values = request.split("&");

        for (var value : values) {
            String[] field = value.split("=");
            fields.put(field[0], field[1]);
        }

        return fields;
    }
}
