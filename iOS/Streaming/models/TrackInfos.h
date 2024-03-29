//
//  TrackInfos.h
//  Streaming
//
//  Created by Paolo on 31/12/12.
//
//

#import <Foundation/Foundation.h>

@interface TrackInfos : NSObject
{
	@private NSString* author;
	@private NSString* title;
	@private UIImage* cover;
}

@property (strong, readwrite) NSString* author;
@property (strong, readwrite) NSString* title;
@property (strong, readwrite) UIImage* cover;

- (void) clean;
- (void) setTrackInfos:(TrackInfos *)trackInfos;
- (BOOL) isClean;

@end
