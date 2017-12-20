
const functions = require('firebase-functions')
const admin = require('firebase-admin')
admin.initializeApp(functions.config().firebase)
const ref = admin.database().ref()
var food
  exports.checkIfTheOrderHasExpired = functions.https.onRequest((req, res) => {
  ref.child('Food').once('value').then(function(snapshot) {
     const currentTime = new Date().getTime()

     food = snapshot.val()
     
    

    const when = food.expiryTime
    const now = food.timeStamp
      if(currentTime >= now - when){
        console.log('time ')
      }


    // res.status(200).send(food);
    }).catch(error => {
       
    this.errorMessage = 'Error - ' + error.message
    res.status(500).send('error');

  })


  res.status(500).send(food);

})





//exports.checkIfTheOrderHasExpired = functions.https.onRequest((req, res) => {
   // .ref('/Food/{userId}/{pushId}')
  //  .onUpdate(event => {
      //  const food = event.data.val()
        //if (food.isExpired) {
          //  return 0
        //}
     
   
  
     

        //food.checkIfOrderIsActive = checkingIfOrderIsExpired(food.timeStamp, food.expiryTime)
       // food.isExpired = true

        //console.log('Status '+food.checkIfOrderIsActive)

        //const promise = event.data.ref.set(food)

        //return promise

  //  })
// function getUsers(userIds = []){
//     return rp(options).then(resp => {
//         if (!resp.users) {
//           return userIds;
//         }
//            });

// }

// function checkingIfOrderIsExpired(now, when) {

//     var currentTime = new Date().getTime()
//     var isOrderExpired = true
//     if (currentTime >= now - when) {
//         isOrderExpired = false
//         console.log('Expired')

//      }


//     return isOrderExpired
// }