# Network Fix for Physical Devices

If the app shows **"Failed to load movies"** when running on a physical device using mobile data, your carrier may be blocking DNS lookups for `api.themoviedb.org`.

## Fix: Change Private DNS to Google

1. Open **Settings** on your phone
2. Go to **Connections** (or **Network & internet**)
3. Tap **More connection settings**
4. Tap **Private DNS**
5. Select **"Private DNS provider hostname"**
6. Type: `dns.google`
7. Tap **Save**
8. Reopen the app — it should load movies now

## Why this happens

Some mobile carriers (especially in India) use their own DNS servers that can block or fail to resolve certain API domains like `api.themoviedb.org`. Switching to Google's DNS (`dns.google`) bypasses the carrier's DNS and resolves the domain correctly.

## Other alternatives

- **Use Wi-Fi** instead of mobile data
- **Use the Android Emulator** which shares your computer's internet directly
