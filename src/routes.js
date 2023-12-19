// Import necessary modules and controllers
import express from "express";
import { body, header, param, validationResult } from "express-validator";
import controller from "./controller.js"; // Import your controller module
import imgUpload from "../modules/imgUpload.js";

const { Router } = express;
const routes = Router();

// Validate middleware
export const validate = (req, res, next) => {
  const errors = validationResult(req);
  if (errors.isEmpty()) {
    return next(); // Validation passed
  }

  const formattedErrors = errors.array().map((error) => ({
    [error.param]: error.msg,
  }));

  res.status(400).json({ errors: formattedErrors });
};

// Token Validation Rule
const tokenValidation = (isRefresh = false) => {
  let refreshText = isRefresh ? "Refresh" : "Authorization";

  return [
    header("Authorization", `Please provide your ${refreshText} token`)
      .exists()
      .not()
      .isEmpty()
      .custom((value, { req }) => {
        if (!value.startsWith("Bearer") || !value.split(" ")[1]) {
          throw new Error(`Invalid ${refreshText} token`);
        }
        if (isRefresh) {
          req.headers.refresh_token = value.split(" ")[1];
          return true;
        }
        req.headers.access_token = value.split(" ")[1];
        return true;
      }),
  ];
};

// Register a new User
routes.post(
  "/signup",
  [
    body("name")
      .trim()
      .not()
      .isEmpty()
      .withMessage("Name must not be empty.")
      .isLength({ min: 3 })
      .withMessage("Name must be at least 3 characters long")
      .escape(),
    body("email", "Invalid email address.")
      .trim()
      .isEmail()
      .custom(async (email) => {
        // Sample custom validation logic (replace with your actual logic)
        const isExist = await controller.checkIfUserExists(email);
        if (isExist)
          throw new Error("A user already exists with this e-mail address");
        return true;
      }),
    body("password")
      .trim()
      .isLength({ min: 4 })
      .withMessage("Password must be at least 4 characters long"),
  ],
  validate,
  controller.signup
);

// Login user through email and password
routes.post(
  "/login",
  [
    body("email", "Invalid email address.")
      .trim()
      .isEmail()
      .notEmpty()
      .withMessage("Email must not be empty."),
    body("password")
      .trim()
      .notEmpty()
      .withMessage("Password must not be empty."),
  ],
  validate,
  controller.login
);

routes.put(
  "/user/:userId",
  [
    param("userId").trim().notEmpty().escape(),
    body("name")
      .trim()
      .not()
      .isEmpty()
      .withMessage("Name must not be empty.")
      .isLength({ min: 3 })
      .withMessage("Name must be at least 3 characters long")
      .escape(),
  ],
  validate,
  tokenValidation(),
  controller.updateUser
);


// Get the user data by providing the access token
routes.get("/profile", tokenValidation(), validate, controller.getUser);

// Get new access and refresh token by providing the refresh token
routes.get(
  "/refresh",
  tokenValidation(true),
  validate,
  controller.refreshToken
);

// History API endpoint
routes.post(
  "/history",
  tokenValidation(),
  [
    body("taskId").trim().notEmpty().withMessage("TaskId must not be empty.")
  ],
  validate, // Ensure validate middleware is used here
  controller.history
);

routes.get(
  "/history",
  tokenValidation(),
  validate, // Ensure validate middleware is used here
  controller.getHistory
);

routes.get(
  "/history/:idhistory",
  tokenValidation(),
  validate, // Ensure validate middleware is used here
  controller.getHistoryById
);

routes.delete(
  "/history/:historyId",
  [param("historyId").trim().notEmpty().escape()],
  validate,
  tokenValidation(),
  controller.deleteHistory
);

// Create a new task
routes.post(
  "/task",
  [
    body("taskName")
      .trim()
      .not()
      .isEmpty()
      .withMessage("Task name must not be empty.")
      .isLength({ min: 3 })
      .withMessage("Task name must be at least 3 characters long")
      .escape(),
    body("taskDescription")
      .trim()
      .isLength({ min: 4 })
      .withMessage("Task description must be at least 4 characters long")
      .escape(),
    body("statusTask")
      .isBoolean()
      .withMessage("Status must be a boolean value."),
  ],
  validate, // Ensure validate middleware is used here
  tokenValidation(),
  controller.createTask
);

// Get all tasks
routes.get("/task", tokenValidation(), validate, controller.getTasks);

//Get a task by Status
routes.get(
  "/task/status/:statusTask",
  [param("statusTask").isBoolean().toBoolean()],
  validate,
  tokenValidation(),
  controller.getTaskByStatus
);

// Get a task by ID
routes.get(
  "/task/:taskId",
  [param("taskId").trim().notEmpty().escape()],
  validate,
  tokenValidation(),
  controller.getTaskById
);

// Update a task by ID
routes.put(
  "/task/:taskId",
  [
    param("taskId").trim().notEmpty().escape(),
    body("taskName")
      .trim()
      .not()
      .isEmpty()
      .withMessage("Task name must not be empty.")
      .isLength({ min: 3 })
      .withMessage("Task name must be at least 3 characters long")
      .escape(),
    body("taskDescription")
      .trim()
      .isLength({ min: 4 })
      .withMessage("Task description must be at least 4 characters long")
      .escape(),
      body("statusTask")
      .optional() // Status is optional in update
      .isBoolean()
      .withMessage("Status must be a boolean value."),
  ],
  validate,
  tokenValidation(),
  controller.updateTask
);

// Delete a task by ID
routes.delete(
  "/task/:taskId",
  [param("taskId").trim().notEmpty().escape()],
  validate,
  tokenValidation(),
  controller.deleteTask
);

export default routes;
