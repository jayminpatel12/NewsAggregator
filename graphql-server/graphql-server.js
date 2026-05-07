// ============================================================
// GraphQL Proxy Server for NewsAPI.org
// ============================================================
// This wraps the NewsAPI REST endpoints in a GraphQL schema
// so the Android app can query news via Apollo Kotlin.
//
// SETUP:
//   npm init -y
//   npm install @apollo/server graphql node-fetch
//   node graphql-server.js
//
// Then runs on http://localhost:4000/graphql
// Android emulator accesses it via http://10.0.2.2:4000/graphql
// ============================================================

const { ApolloServer } = require("@apollo/server");
const { startStandaloneServer } = require("@apollo/server/standalone");

const NEWS_API_KEY = "YOUR_NEWS_API_KEY"; // Same key from Constants.kt
const BASE_URL = "https://newsapi.org/v2";

const typeDefs = `
  type Query {
    topHeadlines(
      country: String = "us"
      category: String = "general"
      page: Int = 1
      pageSize: Int = 20
    ): ArticleResponse!

    searchArticles(
      query: String!
      page: Int = 1
      pageSize: Int = 20
    ): ArticleResponse!
  }

  type ArticleResponse {
    status: String!
    totalResults: Int!
    articles: [Article!]!
  }

  type Article {
    title: String
    description: String
    content: String
    url: String!
    urlToImage: String
    publishedAt: String!
    source: Source!
    author: String
  }

  type Source {
    id: String
    name: String!
  }
`;

const resolvers = {
  Query: {
    topHeadlines: async (_, { country, category, page, pageSize }) => {
      const url = `${BASE_URL}/top-headlines?country=${country}&category=${category}&page=${page}&pageSize=${pageSize}&apiKey=${NEWS_API_KEY}`;
      const res = await fetch(url);
      return res.json();
    },
    searchArticles: async (_, { query, page, pageSize }) => {
      const url = `${BASE_URL}/everything?q=${encodeURIComponent(query)}&page=${page}&pageSize=${pageSize}&sortBy=publishedAt&apiKey=${NEWS_API_KEY}`;
      const res = await fetch(url);
      return res.json();
    },
  },
};

async function startServer() {
  const server = new ApolloServer({ typeDefs, resolvers });
  const { url } = await startStandaloneServer(server, { listen: { port: 4000 } });
  console.log(`GraphQL server ready at ${url}`);
}

startServer();
