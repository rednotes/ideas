-- :name create-idea! :! :n
-- :doc creates a new idea entry
INSERT INTO ideas
(title, description, status)
VALUES (:title, :description, :status)

-- :name get-idea :? :1
-- :doc returns idea by id
SELECT * FROM ideas
WHERE id = :id

-- :name get-all-ideas :? :*
-- :doc returns idea by id
SELECT *
-- id, title, description
FROM ideas WHERE status = 0

-- :name delete-idea! :! :n
-- :doc delete an idea given the id
DELETE FROM ideas
WHERE id = :id
