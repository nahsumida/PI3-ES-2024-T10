import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const app = admin.initializeApp();
const db = app.firestore();
// const colUnidadeLocacao = db.collection("unidadeLocacao");
const colPessoa = db.collection("pessoa");
// const colLocacao = db.collection("locacao");

export const addSamplePerson = functions
  .region("southamerica-east1")
  .https.onRequest(async (request, response) => {
    const pessoa = {
      nome: "natalia firebase",
      dataNascimento: "16 de abril de 2024 às 00:00:00 UTC-3",
      ehGerente: false,
      email: "pi@gmail.com",
      cpf: "12312312345",
      senha: "augdfau",
      telefone: "99999999",
    };
    try {
      const docRef = await colPessoa.add(pessoa);
      response.send("pessoa inserida com sucesso. Referencia: " + docRef.id);
    } catch (e) {
      functions.logger.error("Erro ao inserir pessoa de exemplo");
    }
  });

export const deletePerson = functions
  .region("southamerica-east1")
  .https.onRequest(async (request, response) => {
    try {
      const pessoaId = "7k9WSM8ISZg23ka1aNTX";
      await colPessoa.doc(pessoaId).delete();
      response.send("pessoa deletada com sucesso.");
    } catch (e) {
      functions.logger.error("Erro ao deletar pessoa");
    }
  });

// buscar pessoa com email x
export const searchPerson = functions
  .region("southamerica-east1")
  .https.onRequest(async (request, response) => {
    if (request.method !== "GET") {
      response.status(405).send("Metodo http não permitido");
    }

    try {
      // chamado de snapshot, pois eles podem ser alterados
      // simultaneamente, ou seja temos uma
      // copia de como estavam no momento da consulta
      const snapshot = await colPessoa
        .where("email", "==", "pi@gmail.com")
        .get();
      const pessoa : any = [];
      snapshot.forEach((doc) => {
        pessoa.push(doc.data());
      });
      response.status(200)
        .json(pessoa);
    } catch (e) {
      functions.logger.error("Erro ao buscar pessoa");
    }
  });
