export default function Challenge() {
  return (
    <>
    <form action="/restock-cost" method="POST" id="myForm">
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
        <tbody id="candies"></tbody>
        <tr>
            <td>Re-Order Total Cost:</td>
            <td>$totalCost</td>
        </tr>
      </table>

      <div style={{marginTop: 10}}>
        <a class="btn btn-primary" href="http://localhost:8080/low-stock">Get Low-Stock Items</a>
        <button style={{marginLeft: 10}} type="submit" name="submitReorder" class="btn btn-primary">Determine Re-Order Cost</button>
      </div>
      </form>
    </>
  );
}
