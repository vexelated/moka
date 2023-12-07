import express from "express";
import DB from "./src/dbConnection.js";
import routes from "./src/routes.js";
// import * as nanoid from "nanoid";
// import jwt from "jsonwebtoken";
// import crypto from "crypto";

const app = express();
const port = process.env.PORT || 8081;

// Middleware to parse JSON in request bodies
app.use(express.json());

// Sample route to check if the server is running
app.get("/", (req, res) => {
  res.send("Hello World!");
});

// Mount your API routes under the /api prefix
app.use("/", routes);

// Error handling middleware
app.use((err, req, res, next) => {
  err.statusCode = err.statusCode || 500;
  err.message = err.message || "Internal Server Error";
  res.status(err.statusCode).json({
    message: err.message,
  });
});

// Start the server only if the database connection is successful
async function startServer() {
  try {
    // Attempt to connect to the database
    await DB.getConnection();
    console.log("Connected to the database");

    // Start the Express server
    app.listen(port, () => {
      console.log(`Server is running on port ${port}`);
    });
  } catch (err) {
    console.error(`Failed to connect to the database: ${err.message}`);
    process.exit(1); // Exit the process with an error code
  }
}

// Call the async function to start the server
startServer();
