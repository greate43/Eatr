const functions = require('firebase-functions')

const admin = require('firebase-admin')

admin.initializeApp(functions.config().firebase)
//create database ref
var ref = admin.database().ref()
//do a bunch of stuff
exports.expireFood = functions.https.onRequest((req, res) => {
       ref.child('Food').once('value')
       .then(snap =>{
         snap.forEach(childSnap =>{
           const pushId = childSnap.val().pushId

           //boolean checks
           const checkIfFoodIsInDraftMode = childSnap.val().checkIfFoodIsInDraftMode
           const checkIfOrderIsAccepted = childSnap.val().checkIfOrderIsAccepted
           const checkIfOrderIsActive = childSnap.val().checkIfOrderIsActive
           const checkIfOrderIsBooked = childSnap.val().checkIfOrderIsBooked
           const checkIfOrderIsCompleted = childSnap.val().checkIfOrderIsCompleted
           const checkIfOrderIsInProgress = childSnap.val().checkIfOrderIsInProgress
           const checkIfOrderIsPurchased = childSnap.val().checkIfOrderIsPurchased
 //
           const timeStamp = childSnap.val().timeStamp
           const expiryTime = childSnap.val().expiryTime
            
           if (checkIfOrderIsActive
           && !checkIfOrderIsPurchased 
           && !checkIfFoodIsInDraftMode
           && !checkIfOrderIsBooked
           && !checkIfOrderIsInProgress
           && !checkIfOrderIsCompleted ){
            console.log('Order is Active '+pushId)

              if(isExpiryNeeded(timeStamp,expiryTime)){

                console.log('isExpiryNeeded is true ')

                ref.child('Food').child(pushId).update({
                  "checkIfOrderIsActive": false ,
                  "checkIfOrderIsPurchased": false ,
                  "checkIfFoodIsInDraftMode": false ,
                  "checkIfOrderIsBooked": false ,
                  "checkIfOrderIsInProgress": false ,
                  "checkIfOrderIsCompleted": false 
                })           
              } else {
                console.log('isExpiryNeeded is false ')
               }
        }
         })
        })
      
        console.log('end ')

        res.status(200).send('Status ok')
      })

      function isExpiryNeeded(added,  when) {
        var now = Date.now()
        console.log('now time '+now)
         
        var difference =  (1 * (when - added))
        console.log('difference '+difference)
        var calculateExpiry =  (added + difference)
        console.log('calculateExpiry '+calculateExpiry)
        
        console.log('Is Order Expired '+now > calculateExpiry)
         
        

         return now >= calculateExpiry
       }