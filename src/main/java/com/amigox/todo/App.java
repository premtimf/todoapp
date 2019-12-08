package com.amigox.todo;


import com.amigox.todo.Entity.*;
import com.amigox.todo.Util.HashUtil;
import com.amigox.todo.api.ApiTodo;
import com.amigox.todo.api.ApiUser;
import com.amigox.todo.api.ApiUserService;
import com.amigox.todo.net.ServiceGenerator;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import retrofit2.Response;
import spark.ModelAndView;
import spark.template.handlebars.HandlebarsTemplateEngine;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
            res.redirect("" + username + "/todo");
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

        get("/todo", (req, res) -> {

                var model = Map.of("name", "All Todos", "todos", new TodoDao().getAll());
                return render(model, "todos.hbs");


        });

//TODO:        get("api/todo", (req, res) -> {
//            res.type("application/json");
//            return new Gson().toJson(StatusResponse)
//        });

        get("/:username/todo", (req, res) -> {
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
                res.redirect("/todos/"+thisUsername);

            }

           return null;

        }));

        get("/updateTodo/:slug", (req, res) -> {

            String todoSlug = req.params(":slug");

            var allLabels = new LabelDao().getAll();
            Optional<Todo> todo = new TodoDao().findBySlug(todoSlug);

            if (!todo.isEmpty()) {

                var model = Map.of("name", "TODO", "labels", allLabels, "todo", todo);

                return render(model, "updateTodo.hbs");
            }
            return null;
        });

//        put("/updateTodo/, (req, res) -> {
//
//               String idString = req.splat()[0];
//               System.out.println(idString);
//               Integer id = Integer.parseInt(idString);
////            String todoSlug = req.params(":slug");
//            List<NameValuePair> pairs = URLEncodedUtils.parse(req.body(), Charset.defaultCharset());
//            Map<String, String> params = toMap(pairs);
//
//            String thisUsername = req.session().attribute("user");
//            Optional<User> myUser = new UserDao().findByUsername(thisUsername);
//
//            if (!myUser.isEmpty()) {
//                Optional<Todo> todo = new TodoDao().findById(id);
//                if (!todo.isEmpty()) {
//                    Todo updatedTodo = new Todo();
//                    updatedTodo.setTitle(params.get("todoTitle"));
//                    updatedTodo.setNote(params.get("todoNote"));
//                    updatedTodo.setPriority(Integer.parseInt(params.get("priority")));
////                    updatedTodo.setUser(myUser.get());
//                    //                newTodo.setLabels(params.get("labels"));
//
//
//                    new TodoDao().update(updatedTodo);
//                    res.redirect("/todos");
//
//                }
//
//            }
//
//            return null;
//        });
        put("/updateTodo/", (req, res) -> {

            String idString = req.splat()[0];
            System.out.println(idString);
            Integer id = Integer.parseInt(idString);
//            String todoSlug = req.params(":slug");
            List<NameValuePair> pairs = URLEncodedUtils.parse(req.body(), Charset.defaultCharset());
            Map<String, String> params = toMap(pairs);

            String thisUsername = req.session().attribute("user");
            Optional<User> myUser = new UserDao().findByUsername(thisUsername);

            if (!myUser.isEmpty()) {
                Optional<Todo> todo = new TodoDao().findById(id);
                if (!todo.isEmpty()) {
                    Todo updatedTodo = new Todo();
                    updatedTodo.setTitle(params.get("todoTitle"));
                    updatedTodo.setNote(params.get("todoNote"));
                    updatedTodo.setPriority(Integer.parseInt(params.get("priority")));
//                    updatedTodo.setUser(myUser.get());
                    //                newTodo.setLabels(params.get("labels"));


                    new TodoDao().update(updatedTodo);
                    res.redirect("/todos");

                }

            }

            return null;
        });

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

        path("/api", () -> {
            Gson gson = new Gson();

            get("/user/my_repos", (req, res) -> {
                ApiUserService service = ServiceGenerator.createAuthorizedService(ApiUserService.class);
                Response<JsonArray> myTodos = service.getMyTodos("private").execute();

                Type listType = new TypeToken<List<ApiTodo>>() {}.getType();
                return new Gson().fromJson(myTodos.body(), listType);
            }, gson::toJson);

            get("/users/:username", (req, res) -> {
                String username = req.params(":username");
                ApiUserService service = ServiceGenerator.createService(ApiUserService.class);
                ApiUser user = null;

                try {
                    Response<ApiUser> response = service.getByUsername(username).execute();
                    user = response.body();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return user;
            }, gson::toJson);
            get("/users/:username/todo", (req, res) -> {
                String username = req.params(":username");
                ApiUserService service = ServiceGenerator.createService(ApiUserService.class);

                try {
                    Response<JsonArray> response = service.getUserTodo(username).execute();
                    ApiTodo todo = gson.fromJson(response.body(), ApiTodo.class);
                    System.out.println(todo.getTitle());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return "";
            });
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