# navisens-indoor-location-provider-android
Provider based on MotionDNA SDK from Navisens. Locate your phone using the built-in gyroscope and accelerometer.

## Use

Instantiate the provider with an IndoorLocationProvider as source provider and your Navisens Developper Key. Navisens provider is able to lock onto each updates of the source provider.
Get your dev key on: https://www.navisens.com
```
ILNavisensProvider = new IndoorLocationProviderNavisens(ILManualProvider);
```

Set the provider in your Mapwize SDK:

```
mapwizePlugin.setLocationProvider(ILNavisensProvider);     
```

## Demo

Tap on the screen to set your location.

## Contribute

Contributions are welcome. We will be happy to review your PR.

## Support

For any support with this provider, please do not hesitate to contact [support@mapwize.io](mailto:support@mapwize.io)

## License

MIT
