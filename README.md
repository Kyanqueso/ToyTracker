# Toynado

**Toynado** is an offline desktop inventory tracking application for managing toy orders, payments, and supplier information. Built using JavaFX and SQLite, it’s designed for simplicity, speed, and portability.

---

## Features

- Add, edit, and delete toy records
- Image upload and preview
- Track order and receive dates
- Record cost, payment status, and discounts
- Checkbox selection with total amount calculation
- Search and filter toy entries
- Statistics and insights
- CSV import/export support (auto-generates ID)

---

## Database Schema

Database File: toynado.db
SQLite table: `toys`

| Column         | Type    | Description                                            |
| -------------- | ------- | ------------------------------------------------------ |
| `id`           | INTEGER | Auto-incremented primary key                           |
| `name`         | TEXT    | Toy name *(required)*                                  |
| `date_order`   | TEXT    | Date the toy was ordered (format: `yyyy-MM-dd`)        |
| `date_receive` | TEXT    | Date the toy was received (format: `yyyy-MM-dd`)       |
| `brand_name`   | TEXT    | Brand name of the toy                                  |
| `category`     | TEXT    | Category (e.g., Action Figure, Doll, Puzzle)           |
| `supplier`     | TEXT    | Supplier name *(required)*                             |
| `amount`       | REAL    | Total cost of the toy                                  |
| `downpayment`  | REAL    | Amount already paid                                    |
| `discount`     | REAL    | Discount percentage (default: `0.05`)                  |
| `balance`      | REAL    | Remaining balance after discount and downpayment       |
| `fully_paid`   | TEXT    | Payment status: `"YES"` or `"NO"` *(required)*         |
| `barcode`      | TEXT    | Optional barcode for quick identification              |
| `image_path`   | TEXT    | Absolute path to the toy's image                       |
| `selected`     | INTEGER | Whether the checkbox is ticked  (0 = no, 1 = yes)      |

---

## Distribution
If you may need access to the zip file of Toynado, please email to charlesque404@gmail.com

Moreover, this project is just for personal use.
