const supertest = require("supertest");
const server = require("./server");
const requestWithSupertest = supertest(server);

describe("Express App Tests", () => {
  it("GET /transactions should return transactions", async () => {
    const res = await requestWithSupertest.get(
      "/tsapi/v2/transactions?relativeDateType=CURRENT_DAY&accountIds=000000010975001,000000011008182,000000010975514,000000010975001,000000010900009"
    );
    expect(res.status).toEqual(200);
    expect(res.type).toEqual(expect.stringContaining("json"));
  });
  it("POST /balances should return balances", async () => {
    const res = await requestWithSupertest.post("/accessapi/balance").send({
      accountList: [
        {
          accountId: "000000010962009",
        },
        {
          accountId: "000000011153244",
        },
        {
          accountId: "000000010962009",
        },
        {
          accountId: "000000010900009",
        },
        {
          accountId: "000000011315421",
        },
      ],
    });
    expect(res.status).toEqual(200);
    expect(res.type).toEqual(expect.stringContaining("json"));
  });
  it("GET /payments should return a new payment", async () => {
    const res = await requestWithSupertest
      .post("/digitalSignature/tsapi/v1/payments")
      .send({
        payments: {
          requestedExecutionDate: "2023-09-04",
          paymentAmount: 25,
          paymentType: "RTP",
          paymentIdentifiers: {
            endToEndId: "uf16938248603981123",
          },
          paymentCurrency: "EUR",
          transferType: "CREDIT",
          debtor: {
            debtorName: "OBGLRTPCL1 Account",
            debtorAccount: {
              accountId: "DE88501108006231400596",
              accountCurrency: "EUR",
              accountType: "IBAN",
            },
          },
          debtorAgent: {
            financialInstitutionId: {
              bic: "CHASDEFX",
            },
          },
          creditor: {
            creditorName: "UNICORNUAT Account",
            creditorAccount: {
              accountId: "DE45501108000041287103",
              accountCurrency: "EUR",
            },
          },
          creditorAgent: {
            financialInstitutionId: {
              bic: "CHASDEFX",
            },
          },
        },
      });
    expect(res.status).toEqual(200);
    expect(res.type).toEqual(expect.stringContaining("json"));
  });
});
