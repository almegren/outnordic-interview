# Backend case

Hi and welcome to our case!

## User story

We have the following user stories:

_As a customer, I would like to see the status of my orders, so I know which is on the way_
_As a customer, I want to know what I have previously paid for an item in an earlier order_

## Backend task

As a backend developer your task is to create an API that the frontend team can use in order to fetch the required information in order to solve these stories.

Feel free to start on something already, and upload it to a public github repo before the interview. Technologies is up to you.
In the interview we will talk about these stories and pair program a little on what is remaining in order to solve them.

## Data

In the database, a postgreSQL database, we will have some dummy data available.
The easiest to get going is to run:
`docker compose up`

This will set up a local instance of postgresql.

Then running `lein run` will seed that database with some dummy data.
