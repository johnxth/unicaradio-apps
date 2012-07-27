//
//  FirstViewController.m
//  Streaming
//
//  Created by Paolo on 22/07/12.
//  Copyright (c) 2012 __MyCompanyName__. All rights reserved.
//

#import "StreamingViewController.h"
#import "AppDelegate.h"

@interface StreamingViewController ()

@end

@implementation StreamingViewController

@synthesize titleLabel;
@synthesize singerLabel;
@synthesize playPauseButton;
@synthesize coverImageView;

@synthesize currentTitle;
@synthesize currentArtist;

#pragma mark - Controller lifecycle

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        self.title = NSLocalizedString(@"On Air", @"On Air");
        self.tabBarItem.image = [UIImage imageNamed:@"first"];
    }
    NSLog(@"init streaming view controller");
    return self;
}
							
- (void)viewDidLoad
{
    [super viewDidLoad];
	// Do any additional setup after loading the view, typically from a nib.
}

- (void)viewDidUnload
{
    [super viewDidUnload];
    // Release any retained subviews of the main view.
}

- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation
{
    if ([[UIDevice currentDevice] userInterfaceIdiom] == UIUserInterfaceIdiomPhone) {
        return (interfaceOrientation != UIInterfaceOrientationPortraitUpsideDown);
    } else {
        return YES;
    }
}

- (void)dealloc
{
    [self destroyStreamer];
	[super dealloc];
}

#pragma mark - Actions

- (IBAction) playOrPause:(id)sender
{
    NSLog(@"playOrPause");
    [self clearCurrentData];

    if([streamer isPlaying]) {
        [streamer stop];
        [self destroyStreamer];
        [playPauseButton setImage:[UIImage imageNamed:PLAY_IMAGE_NORMAL] forState:UIControlStateNormal];
        [playPauseButton setImage:[UIImage imageNamed:PLAY_IMAGE_PRESSED] forState:UIControlStateHighlighted];
    } else {
        [self createStreamer];
        [streamer start];
        [playPauseButton setImage:[UIImage imageNamed:PAUSE_IMAGE_NORMAL] forState:UIControlStateNormal];
        [playPauseButton setImage:[UIImage imageNamed:PAUSE_IMAGE_PRESSED] forState:UIControlStateHighlighted];
    }
    [self updateUi];
}

#pragma mark - Notification receivers

- (void)metadataChanged:(NSNotification *)aNotification
{
	NSString *streamArtist;
	NSString *streamTitle;
    NSLog(@"Raw meta data = %@", [[aNotification userInfo] objectForKey:@"metadata"]);
    
	NSArray *metaParts = [[[aNotification userInfo] objectForKey:@"metadata"] componentsSeparatedByString:@"';"];
	NSString *item;
	NSMutableDictionary *hash = [[NSMutableDictionary alloc] init];
	for (item in metaParts) {
		// split the key/value pair
		NSArray *pair = [item componentsSeparatedByString:@"="];
		// don't bother with bad metadata
		if ([pair count] == 2)
			[hash setObject:[pair objectAtIndex:1] forKey:[pair objectAtIndex:0]];
	}
    
	// do something with the StreamTitle
	//NSString *streamString = [[hash objectForKey:@"StreamTitle"] stringByReplacingOccurrencesOfString:@"'" withString:@""];
    NSString *streamString = [hash objectForKey:@"StreamTitle"];
	
	NSArray *streamParts = [streamString componentsSeparatedByString:@" - "];
	if ([streamParts count] > 0) {
		streamArtist = [[streamParts objectAtIndex:0] substringFromIndex:1];
	} else {
		streamArtist = @"";
	}
	// this looks odd but not every server will have all artist hyphen title
	if ([streamParts count] >= 2) {
		streamTitle = [streamParts objectAtIndex:1];
	} else {
		streamTitle = @"";
	}
	NSLog(@"%@ by %@", streamTitle, streamArtist);

	self.currentArtist = streamArtist;
	self.currentTitle = streamTitle;

    [self updateUi];
}

//
// playbackStateChanged:
//
// Invoked when the AudioStreamer
// reports that its playback status has changed.
//
- (void)playbackStateChanged:(NSNotification *)aNotification
{
	/*
    iPhoneStreamingPlayerAppDelegate *appDelegate = [[UIApplication sharedApplication] delegate];
    
	if ([streamer isWaiting])
	{
		if (appDelegate.uiIsVisible) {
			[levelMeterView updateMeterWithLeftValue:0.0 
                                          rightValue:0.0];
			[streamer setMeteringEnabled:NO];
			[self setButtonImage:[UIImage imageNamed:@"loadingbutton.png"]];
		}
	}
	else if ([streamer isPlaying])
	{
		if (appDelegate.uiIsVisible) {
			[streamer setMeteringEnabled:YES];
			[self setButtonImage:[UIImage imageNamed:@"stopbutton.png"]];
		}
	}
	else if ([streamer isPaused]) {
		if (appDelegate.uiIsVisible) {
			[levelMeterView updateMeterWithLeftValue:0.0 
                                          rightValue:0.0];
			[streamer setMeteringEnabled:NO];
			[self setButtonImage:[UIImage imageNamed:@"pausebutton.png"]];
		}
	}
	else if ([streamer isIdle])
	{
		if (appDelegate.uiIsVisible) {
			[levelMeterView updateMeterWithLeftValue:0.0 
                                          rightValue:0.0];
			[self setButtonImage:[UIImage imageNamed:@"playbutton.png"]];
		}
		[self destroyStreamer];
	}
    */
}

#pragma mark - Other functions

- (void) createStreamer
{
    if(streamer != nil) {
        [self destroyStreamer];
    }

    NSURL *url = [NSURL URLWithString:SERVER_URL];
    streamer = [[AudioStreamer alloc] initWithURL:url];

    [[NSNotificationCenter defaultCenter]
	 addObserver:self
	 selector:@selector(metadataChanged:)
	 name:ASUpdateMetadataNotification
	 object:streamer];
    [[NSNotificationCenter defaultCenter]
     addObserver:self
     selector:@selector(playbackStateChanged:)
     name:ASStatusChangedNotification
     object:streamer];
}

- (void) destroyStreamer
{
	if (streamer)
	{
		[[NSNotificationCenter defaultCenter]
         removeObserver:self
         name:ASStatusChangedNotification
         object:streamer];

		[streamer stop];
		[streamer release];
		streamer = nil;
	}
}

- (void) clearUi
{
    NSLog(@"clearUi");

    UIImage *image = [UIImage imageNamed:DEFAULT_COVER_FILENAME];
    coverImageView.image = image;
    titleLabel.text = @"";
    singerLabel.text = @"- Unicaradio -";
}

- (void) clearCurrentData
{
    NSLog(@"clearCurrentData");
    self.currentTitle = @"";
    self.currentArtist = @"- Unicaradio -";
}

- (void) updateUi
{
    NSLog(@"updateUi");
    [self clearUi];

    // draw cover, title and singer
    AppDelegate *appDelegate = (AppDelegate *) [[UIApplication sharedApplication] delegate];
	if (appDelegate.uiIsVisible) {
		singerLabel.text = self.currentArtist;
        titleLabel.text = self.currentTitle;
        
        if(streamer != nil && ([streamer isPlaying] || [streamer isWaiting])) {
            NSURL *url = [NSURL URLWithString:COVER_URL];
            UIImage *image = [UIImage imageWithData: [NSData dataWithContentsOfURL:url]];
            coverImageView.image = image;
        }
	}
}

@end
