// Gunakan sintaks ESM pada Node.js dengan ekstensi .mjs
import { Storage } from '@google-cloud/storage';
import dateFormat from 'dateformat';
import path from 'path';
const pathKey = path.resolve('./serviceaccountkey.json');

// TODO: Sesuaikan konfigurasi Storage
const gcs = new Storage({
    projectId: 'capstone-moka',
    keyFilename: pathKey
});

// TODO: Tambahkan nama bucket yang digunakan
const bucketName = 'fotoprofile-dbmoka';
const bucket = gcs.bucket(bucketName);

export const getPublicUrl = (req, res, next) => {
    return `https://storage.googleapis.com/${bucketName}/${filename}`;
}

export const uploadToGcs = (req, res, next) => {
    if (!req.file) return next();

    const gcsname = dateFormat(new Date(), 'yyyymmdd-HHMMss');
    const file = bucket.file(gcsname);

    const stream = file.createWriteStream({
        metadata: {
            contentType: req.file.mimetype,
        },
    });

    stream.on('error', (err) => {
        req.file.cloudStorageError = err;
        next(err);
    });

    stream.on('finish', () => {
        req.file.cloudStorageObject = gcsname;
        req.file.cloudStoragePublicUrl = getPublicUrl(gcsname);
        next();
    });

    stream.end(req.file.buffer);
}

const ImgUpload = {
    getPublicUrl,
    uploadToGcs,
};

export default ImgUpload;
