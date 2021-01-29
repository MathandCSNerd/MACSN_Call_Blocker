# MACSN_Call_Blocker
This repo is for a simple call blocker for android phones.

The reason I decided to create this, is due to my complete disatisfaction with similar software available on the google play store.

The UI is about as minimalistic as it gets since I didn't want to spend too much time on this little project. Considering java android development is not something I've done much of, I didn't really want to do anything more ambitious than the bare minimum required.



# Building
To build the project, just use something like:

./gradlew assembleDebug

I didn't add anything fancy to the build system for the same reasons stated above. It's pretty much the vanilla setup from a android studio project template. Therefore, the usual gradle tasks are there and they will function as expected.


# Usage
As I said before, the UI is very minimalistic.

Upon the original launch of the application, it will make the permissions requests to the android os and, assuming it didn't already have the permissions granted, it will close.

When launched with the proper permissions granted, it will bring up the "Blocked Numbers" list. To navigate to the other lists, simply use the "next" and "previous" buttons.

To add a number to the list simply type it in the box and click the square button. Clicking the "play" button will add it to the "Allowed Numbers" list.

To remove a number from any list, simply tap it.

There is a drop down menu where you can choose to temporarily stop blocking, (dis)allow contacts, or refresh the contacts list from the phone.


# Blocking Rules
If the blocking is currently disabled (by the menu option), it will be allowed.

If the number is not on the blocklist, it will be allowed.

If the number is on the blocklist, and it is on the allowed list, it will still be allowed.

If the number is on the blocklist, and it is in the phone's contacts list and the option to "Allow Contacts" is set to true, it will still be allowed.

Otherwise, the number will be blocked.

The blocking/allowing lists both support the * and ? wildcards which function similar to the ones in GNU's Bash. For example, adding just the entry \"*\" to the allow list will allow all numbers, whereas adding "555555????" will allow all numbers starting with 555-5555. On the other hand, adding \"*\" to the block list causes all numbers to be blocked unless they are on the allow list or contacts.

When incoming call is identified as a blocked number, the call is disconnected and the incoming number is added to the "Rejected Numbers" list.
