export default function Challenge() {
  return (
    <>
    <form method="POST" id="myForm">
      <table>
        <thead>
          <tr>
            <td>SKU</td>
            <td>Item Name</td>
            <td>Amount in Stock</td>
            <td>Capacity</td>
            <td>Order Amount</td>
          </tr>
        </thead>
        <tbody id="candies">

        </tbody>

      </table>

      <div style={{marginTop: 10}}>
        <a class="btn btn-primary" href="http://localhost:8080/low-stock">Get Low-Stock Items</a>
        <button style={{marginLeft: 10}} type="submit" name="submitReorderCost" class="btn btn-primary" formAction="http://localhost:8080/restock-cost">Determine Re-Stock Cost</button>
        <button style={{marginLeft: 10}} type="submit" name="submitOrder" class="btn btn-primary" formAction="http://localhost:8080/submit-order">Submit Order</button>
      </div>
    </form>
    </>
  );
}
