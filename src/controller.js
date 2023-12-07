import bcrypt from "bcrypt";
import * as nanoid from "nanoid";
import { createHash } from "crypto";
import { validationResult, matchedData } from "express-validator";
import { generateToken, verifyToken } from "./tokenHandler.js";
import DB from "./dbConnection.js";

const checkIfUserExists = async (email) => {
  try {
    // Your logic to check if the user exists in the database
    const [result] = await DB.execute(
      "SELECT * FROM `users` WHERE `email` = ?",
      [email]
    );
    return result.length > 0;
  } catch (error) {
    console.error("Error in checkIfUserExists:", error);
    throw error;
  }
};

const validation_result = validationResult.withDefaults({
  formatter: (error) => error.msg,
});

export const validate = (req, res, next) => {
  const errors = validation_result(req).mapped();
  if (Object.keys(errors).length) {
    return res.status(422).json({
      status: 422,
      errors,
    });
  }
  next();
};

// If email already exists in database
export const fetchUserByEmailOrID = async (data, isEmail = true) => {
  let sql = "SELECT * FROM `users` WHERE `email`=?";
  if (!isEmail) sql = "SELECT `id` ,`name`, `email` FROM `users` WHERE `id`=?";
  const [row] = await DB.execute(sql, [data]);
  return row;
};

export default {
  signup: async (req, res, next) => {
    try {
      const { name, email, password } = matchedData(req);

      const saltRounds = 10;
      // Hash the password
      const hashPassword = await bcrypt.hash(password, saltRounds);

      // Store user data in the database
      const [result] = await DB.execute(
        "INSERT INTO `users` (`name`,`email`,`password`) VALUES (?,?,?)",
        [name, email, hashPassword]
      );
      res.status(201).json({
        status: 201,
        message: "You have been successfully registered.",
        user_id: result.insertId,
      });
    } catch (err) {
      next(err);
    }
  },

  login: async (req, res, next) => {
    try {
      const { email, password } = req.body;

      // Ensure that 'email' and 'password' are defined
      if (!email || !password) {
        return res.status(422).json({
          status: 422,
          message: "Email and password are required.",
        });
      }

      // Replace 'user' with 'email' in the query
      const [result] = await DB.execute(
        "SELECT `id`, `email`, `password` FROM `users` WHERE `email`=?",
        [email]
      );

      if (result.length === 0) {
        return res.status(401).json({
          status: 401,
          message: "Authentication failed: User not found.",
        });
      }

      const user = result[0];
      const verifyPassword = await bcrypt.compare(password, user.password);

      if (!verifyPassword) {
        return res.status(401).json({
          status: 401,
          message: "Authentication failed: Incorrect password.",
        });
      }

      // Generating Access and Refresh Token
      const access_token = generateToken({ id: user.id });
      const refresh_token = generateToken({ id: user.id }, false);

      const md5Refresh = createHash("md5").update(refresh_token).digest("hex");

      // Storing refresh token in MD5 format
      const [insertResult] = await DB.execute(
        "INSERT INTO `refresh_tokens` (`user_id`, `token`) VALUES (?, ?)",
        [user.id, md5Refresh]
      );

      if (!insertResult.affectedRows) {
        throw new Error("Failed to whitelist the refresh token.");
      }

      res.status(200).json({
        status: 200,
        message: "Login successful",
        access_token,
        refresh_token,
      });
    } catch (err) {
      next(err);
    }
  },

  getUser: async (req, res, next) => {
    try {
      // Verify the access token
      const data = verifyToken(req.headers.access_token);
      if (data?.status) return res.status(data.status).json(data);
      // fetching user by the `id` (column)
      const user = await fetchUserByEmailOrID(data.id, false);
      if (user.length !== 1) {
        return res.status(404).json({
          status: 404,
          message: "User not found",
        });
      }
      res.json({
        status: 200,
        user: user[0],
      });
    } catch (err) {
      next(err);
    }
  },

  refreshToken: async (req, res, next) => {
    try {
      const refreshToken = req.headers.refresh_token;
      // Verify the refresh token
      const data = verifyToken(refreshToken, false);
      if (data?.status) return res.status(data.status).json(data);

      // Converting refresh token to md5 format
      const md5Refresh = createHash("md5").update(refreshToken).digest("hex");

      // Finding the refresh token in the database
      const [refTokenRow] = await DB.execute(
        "SELECT * from `refresh_tokens` WHERE token=?",
        [md5Refresh]
      );

      if (refTokenRow.length !== 1) {
        return res.json({
          status: 401,
          message: "Unauthorized: Invalid Refresh Token.",
        });
      }

      // Generating new access and refresh token
      const access_token = generateToken({ id: data.id });
      const refresh_token = generateToken({ id: data.id }, false);

      const newMd5Refresh = createHash("md5")
        .update(refresh_token)
        .digest("hex");

      // Replacing the old refresh token to new refresh token
      const [result] = await DB.execute(
        "UPDATE `refresh_tokens` SET `token`=? WHERE `token`=?",
        [newMd5Refresh, md5Refresh]
      );

      if (!result.affectedRows) {
        throw new Error("Failed to whitelist the Refresh token.");
      }

      res.json({
        status: 200,
        access_token,
        refresh_token,
      });
    } catch (err) {
      next(err);
    }
  },

  history: async (req, res, next) => {
    try {
      const hisId = nanoid.nanoid(10);
      const { taskId, status } = matchedData(req);

      // Save history to the database using Sequelize create method
      // const history = await DB.models.History.create({
      //   idhistory: hisId,
      //   idtask: taskId,
      //   statustask: status,
      // });

      const [history] = await DB.execute(
        "INSERT INTO `history` (`idhistory`,`idtask`,`statustask`) VALUES (?,?,?)",
        [hisId, taskId, status]
      );
      
      res.status(200).json({ error: false, message: "success" });
    } catch (err) {
      console.error(err);
      res.status(500).json({ error: true, message: "Internal Server Error" });
    }
  },

  getHistory: async (req, res, next) => {
    try {
      const [rows] = await DB.execute("SELECT * FROM `history`");
      res.json({
        status: 200,
        message: "All history records displayed successfully",
        data: rows,
      });
    } catch (err) {
      next(err);
    }
  },

  deleteHistory: async (req, res, next) => {
    try {
      const { historyId } = req.params;

      const [row] = await DB.execute(
        "SELECT * FROM `history` WHERE `idhistory`=?",
        [historyId]
      );

      if (row.length === 1) {
        await DB.execute("DELETE FROM `history` WHERE `idhistory`=?", [
          historyId,
        ]);

        res.json({
          status: 200,
          message: "History record deleted successfully",
          data: row[0],
        });
      } else {
        res.status(404).json({
          status: 404,
          message: "History record not found",
        });
      }
    } catch (err) {
      next(err);
    }
  },

  createTask: async (req, res, next) => {
    try {
      const { taskName, taskDescription } = matchedData(req);

      const taskId = nanoid.nanoid(10);
      const taskDate = new Date().toISOString().slice(0, 19).replace("T", " ");

      const [result] = await DB.execute(
        "INSERT INTO `tasks` (`idtask`,`taskname`,`taskdescription`,`taskdate`) VALUES (?,?,?,?)",
        [taskId, taskName, taskDescription, taskDate]
      );

      res.status(201).json({
        status: 201,
        message: "Task created successfully",
        data: {
          taskId: taskId,
        },
      });
    } catch (err) {
      next(err);
    }
  },

  getTasks: async (req, res, next) => {
    try {
      const [rows] = await DB.execute("SELECT * FROM `tasks`");
      res.json({
        status: 200,
        message: "All tasks displayed successfully",
        data: rows,
      });
    } catch (err) {
      next(err);
    }
  },

  getTaskById: async (req, res, next) => {
    try {
      const [row] = await DB.execute("SELECT * FROM `tasks` WHERE `idtask`=?", [
        req.params.taskId,
      ]);

      if (row.length === 1) {
        res.json({
          status: 200,
          message: "Task displayed successfully",
          data: row[0],
        });
      } else {
        res.status(404).json({
          status: 404,
          message: "Task not found",
        });
      }
    } catch (err) {
      next(err);
    }
  },

  updateTask: async (req, res, next) => {
    try {
      const [row] = await DB.execute("SELECT * FROM `tasks` WHERE `idtask`=?", [
        req.params.taskId,
      ]);

      if (row.length === 1) {
        const { taskName, taskDescription } = matchedData(req);

        await DB.execute(
          "UPDATE `tasks` SET `taskname`=?, `taskdescription`=? WHERE `idtask`=?",
          [taskName, taskDescription, req.params.taskId]
        );

        res.json({
          status: 200,
          message: "Task updated successfully",
          data: {
            taskId: req.params.taskId,
          },
        });
      } else {
        res.status(404).json({
          status: 404,
          message: "Task not found",
        });
      }
    } catch (err) {
      next(err);
    }
  },

  deleteTask: async (req, res, next) => {
    try {
      const [row] = await DB.execute("SELECT * FROM `tasks` WHERE `idtask`=?", [
        req.params.taskId,
      ]);

      if (row.length === 1) {
        await DB.execute("DELETE FROM `tasks` WHERE `idtask`=?", [
          req.params.taskId,
        ]);

        res.json({
          status: 200,
          message: "Task deleted successfully",
          data: row[0],
        });
      } else {
        res.status(404).json({
          status: 404,
          message: "Task not found",
        });
      }
    } catch (err) {
      next(err);
    }
  },

  checkIfUserExists,
  // Other exported functions...
};
