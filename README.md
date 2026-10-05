# Payment Integration System

A backend payment processing system built using Java and Spring Boot. The application provides REST APIs to process payments, validate requests, store payment details in PostgreSQL, and prevent duplicate transactions using idempotency keys.

## Features

- Process payment requests through REST APIs
- Store payment transactions in PostgreSQL
- Idempotency support to prevent duplicate payments
- Request validation
- Global exception handling
- Payment status tracking
- Mock payment provider for payment simulation
- Get all payment transactions
- Unit testing using JUnit and Mockito
- API documentation using Swagger/OpenAPI

## Tech Stack

- Java 17
- Spring Boot 4.0.8
- Spring Web
- Spring Data JPA
- Hibernate
- PostgreSQL
- Maven
- JUnit 5
- Mockito
- Swagger / OpenAPI
- Git & GitHub

## Project Architecture

```text
Client
   |
   v
Payment Controller
   |
   v
Payment Service
   |
   +----> Idempotency Check
   |
   +----> Mock Payment Provider
   |
   v
Payment Repository
   |
   v
PostgreSQL Database