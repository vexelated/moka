import mysql from "mysql2/promise"; // Use 'mysql2/promise' for async/await support
import { config } from "dotenv";

config();

const { DB_HOST, DB_PASS, DB_USER, DB_NAME } = process.env;

const dbConfig = {
  host: DB_HOST,
  user: DB_USER,
  password: DB_PASS,
  database: DB_NAME,
  // socketPath: `/cloudsql/${CLOUD_SQL_CONNECTION_NAME}`,
  waitForConnections: true,
  connectionLimit: 10,
  queueLimit: 0,
};

const pool = mysql.createPool(dbConfig);

export default pool;

// import mysql from 'mysql2';
// import { config } from 'dotenv';

// const db = () => {
//     config();
//     const { DB_HOST, DB_NAME, DB_USER, DB_PASSWORD } = process.env;
//     return mysql.createPool({
//         host: DB_HOST,
//         user: DB_USER,
//         password: DB_PASSWORD,
//         database: DB_NAME,
//     });
// };

// export default db().promise();

// import mysql from 'mysql2';
// import { config } from 'dotenv';

// const connection = () => {
//     config();
//     const { DB_HOST, DB_NAME, DB_USER, DB_PASSWORD } = process.env;
//     return mysql.createPool({
//         host: DB_HOST,
//         user: DB_USER,
//         password: DB_PASSWORD,
//         database: DB_NAME,
//     });
// };

// export default connection().promise();
