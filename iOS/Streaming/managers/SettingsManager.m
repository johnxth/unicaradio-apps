//
//  SettingsManager.m
//  Streaming
//
//  Created by Paolo on 09/01/13.
//
//

#import "SettingsManager.h"

@implementation SettingsManager

@synthesize settings;

#pragma mark - Singleton Methods

+ (id) getInstance
{
	static SettingsManager *instance = nil;

    @synchronized(self) {
        if(instance == nil) {
            instance = [[self alloc] init];
		}
    }

    return instance;
}

- (id)init
{
	if(self = [super init]) {
		NSString *filePath = [self getDataFilePath];
		if([[NSFileManager defaultManager] fileExistsAtPath:filePath]) {
			NSLog(@"file exists");
			settings = [[NSMutableDictionary alloc] initWithContentsOfFile:filePath];
		} else {
			NSLog(@"file does not exists");
			settings = [[NSMutableDictionary alloc] init];
		}
	}

	return self;
}

#pragma mark - Instance methods

- (id) getPreference:(NSString *) preference
{
	return [settings objectForKey:preference];
}

- (void) savePreference:(NSString *) preference andValue:(id) value
{
	[settings setObject:value forKey:preference];
	BOOL result = [settings writeToFile:[self getDataFilePath] atomically:YES];

	NSLog(@"result: %@", result ? @"OK" : @"ERROR");
}

- (NSString *) getDataFilePath
{
	NSArray *path = NSSearchPathForDirectoriesInDomains(NSDocumentDirectory, NSUserDomainMask, YES);
	NSString *documentsDirectory = [path objectAtIndex:0];

	return [documentsDirectory stringByAppendingPathComponent:SETTINGS_PLIST];
}

#pragma mark Specific methods

- (NetworkType) getNetworkType
{
	id networkTypeAsId = [self getPreference:PREF_NETWORK_TYPE];

	return [networkTypeAsId intValue];
}

- (NetworkType) getNetworkTypeForCover
{
	id networkTypeAsId = [self getPreference:PREF_COVER_NETWORK];
	
	return [networkTypeAsId intValue];
}

- (void) saveNetworkType:(NetworkType) networkType
{
	[self savePreference:PREF_NETWORK_TYPE andValue:[NSNumber numberWithInt:networkType]];
}

- (void) saveNetworkTypeForCover:(NetworkType) networkType
{
	[self savePreference:PREF_COVER_NETWORK andValue:[NSNumber numberWithInt:networkType]];
}

- (int) getInstalledVersion
{
	id installedVersionAsId = [self getPreference:PREF_INSTALLED_VERSION];

	return [installedVersionAsId intValue];
}

- (void) updateInstalledVersion
{
	NSString *currentVersionId = [[NSBundle mainBundle] objectForInfoDictionaryKey: (NSString *)kCFBundleVersionKey];

	[self savePreference:PREF_INSTALLED_VERSION andValue:[NSNumber numberWithInt:[currentVersionId intValue]]];
}

@end
