import image from "./sugar-glider.png";

export default function Challenge() {
  return (
    <>

    <div class="container">
        <a class="navbar-brand" href="http://localhost:3000">
          <img src={image} alt="" width="70" height="50" class="d-inline-block"></img>
        </a>
        <label style={{fontSize: 30, fontFamily:"Sofia"}} class="font-effect-shadow-multiple text-primary">Randy's Candies</label>
    </div>

    <div class="card">
    <form method="POST" id="myForm">
      <table class="table table-striped table-hover">
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

      <div style={{marginTop: 10}} class="collection">
        <a style={{marginLeft: 10}} class="btn btn-primary" href="http://localhost:8080/low-stock">Get Low-Stock Items</a>
        <button style={{marginLeft: 10}} type="submit" name="submitReorderCost" class="btn btn-primary" formAction="http://localhost:8080/restock-cost">Determine Re-Stock Cost</button>
        <button style={{marginLeft: 10}} type="submit" name="submitOrder" class="btn btn-primary" formAction="http://localhost:8080/submit-order">Submit Order</button>
      </div>
    </form>
    </div>
    </>
  );
}
