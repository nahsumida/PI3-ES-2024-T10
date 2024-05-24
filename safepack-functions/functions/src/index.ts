import * as functions from "firebase-functions";
import * as admin from "firebase-admin";

const app = admin.initializeApp();
const db = app.firestore();
const locacaoCollection = db.collection("locacao");

// adiciona o valor do extorno na locacao
/*
export const addEstornoLocacao = functions
  .region("southamerica-east1")
  .https.onRequest(async (request:any, response:any) => {
    const idLocacao = response.body.idLocacao;
    const valEstorno = response.body.valEstorno;

    try {
      const dataToUpdate = {
        estorno: valEstorno,
      };

      await locacaoCollection.doc(idLocacao).update(dataToUpdate);
      response.send("Estorno atualizado com sucesso");
    } catch (e) {
      functions.logger.error("Erro ao inserir pessoa de exemplo");
    }
  });
*/
export const addEstornoLocacao = functions
  .region("southamerica-east1")
  .https.onRequest(async (request: any, response: any) => {
    const idLocacao = request.body.idLocacao;
    const valEstorno = request.body.valEstorno;

    if (!idLocacao || !valEstorno) {
      response.status(400)
        .send("Os parâmetros idLocacao e valEstorno são obrigatórios.");
      return;
    }

    try {
      const dataToUpdate = {
        estorno: valEstorno,
      };

      await locacaoCollection.doc(idLocacao).set(dataToUpdate, {merge: true});
      response.send("Estorno atualizado com sucesso");
    } catch (e) {
      functions.logger.error("Erro ao atualizar estorno da locação", e);
      response.status(500).send("Erro ao atualizar estorno da locação");
    }
  });
